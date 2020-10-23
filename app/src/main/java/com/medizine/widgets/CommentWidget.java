package com.medizine.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Note: This widget does not store information offline
 */
public class CommentWidget extends FrameLayout implements CommentAdapter.CommentInterface {
    public static final String TAG = "CommentWidget";
    CommentAdapter mCommentAdapter;
    SectionWidget mSectionWidget;
    View mLine;
    String mModule;
    String mModuleId;

    public CommentWidget(Context context) {
        this(context, null);
    }

    public CommentWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentWidget(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_comment, this, true);

        mSectionWidget = findViewById(R.id.sectionWidget);
        final EditText etComment = findViewById(R.id.etComment);
        mLine = findViewById(R.id.line);

        etComment.setScroller(new Scroller(context));
        etComment.setVerticalScrollBarEnabled(true);

        etComment.setOnTouchListener((v, event) -> {
            if (etComment.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });

        // Set Profile pic
        ImageView profilePic = findViewById(R.id.profilePic);
        User user = StorageService.getInstance().getUser();
        ImageUtils.renderCircleUserPicOrInitials(context, user.getProfilePicAsString(), user.getName(), profilePic);

        // Set send button
        final ImageView sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            if (Utils.isNullOrEmpty(etComment.getText().toString())) {
            } else {
                AnalyticsUtils.Events.sendContentSelectionEvent(v.getId(), AnalyticsConstants.Event.SUBMIT, AnalyticsConstants.Content.COMMENT, mModule);
                final NewComment comment = new NewComment();
                comment.setUserId(StorageService.getInstance().getUser().getId());
                comment.setContent(etComment.getText().toString());
                comment.setModule(mModule);
                comment.setModuleId(mModuleId);


                RxNetwork.observeNetworkConnectivity(getContext())
                        .flatMapSingle(connectivity -> {
                            if (connectivity.isAvailable()) {
                                return NetworkService.getInstance().postComment(comment);
                            } else {
                                throw new NetworkUnavailableException();
                            }
                        })
                        .compose(JainamOperators::jainamRetryWhen)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(objectResponse -> {
                                    if (objectResponse.getData() != null) {
                                        etComment.setText("");
                                        loadComments();
                                    } else {
                                    }
                                }, throwable -> {
                                    if (throwable instanceof NetworkUnavailableException) {
                                        Toast.makeText(getContext(), getContext().getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), getContext().getString(R.string.oops_something_went_wrong),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    Utils.logException(TAG, throwable);
                                }

                        );
            }
        });

        // Set recycler view
        mCommentAdapter = new CommentAdapter(getContext(), this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mCommentAdapter);
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext(), Utils.dpToPixels(16)));
    }

    public void init(String module, String moduleId) {
        mModule = module;
        mModuleId = moduleId;

        loadComments();
    }

    public void loadComments() {

        Disposable disposable = RxNetwork.observeNetworkConnectivity(getContext())
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getAllComments(mModuleId, StorageService.getInstance().getUser().getId());
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(JainamOperators::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    List<Comment> mList = response.getData();
                    if (mList != null) {
                        Collections.reverse(mList);
                        setList(mList);
                    }
                }, throwable -> {
                    if (throwable instanceof NetworkUnavailableException) {
                        Toast.makeText(getContext(), getContext().getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.logException(TAG, throwable);
                    }
                });
    }

    public void setList(List<Comment> list) {
        mCommentAdapter.setList(list);
        if (list == null) {
            setTitle(0);
        } else {
            setTitle(list.size());
        }
    }

    @Override
    public void setTitle(int commentCount) {
        if (commentCount == 0) {
            mSectionWidget.setTitle(getContext().getString(R.string.content));
            mLine.setVisibility(GONE);
        } else {
            mSectionWidget.setTitle(commentCount + " " + getContext().getString(R.string.comments));
            mLine.setVisibility(VISIBLE);
        }
    }
}
