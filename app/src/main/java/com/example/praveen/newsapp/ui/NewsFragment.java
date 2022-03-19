package com.example.praveen.newsapp.ui;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.praveen.newsapp.R;
import com.example.praveen.newsapp.adapters.NewsAdapter;
import com.example.praveen.newsapp.databinding.NewsFragmentBinding;
import com.example.praveen.newsapp.models.Article;
import com.example.praveen.newsapp.models.Specification;

import java.util.List;

import timber.log.Timber;

public class NewsFragment extends Fragment implements NewsAdapter.NewsAdapterListener {
    public static final String PARAM_LIST_STATE = "param-state";
    private final NewsAdapter newsAdapter = new NewsAdapter(null, this);
    private NewsFragmentBinding binding;
    private boolean showSaved = true;
    private Parcelable listState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        } else {
            showSaved = true;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.news_fragment, container, false);
        RecyclerView recyclerView = binding.rvNewsPosts;
        recyclerView.setAdapter(newsAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(PARAM_LIST_STATE);
        }
        NewsViewModel viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        if (!showSaved) {
            viewModel.getAllSaved().observeForever(new Observer<List<Article>>() {
                @Override
                public void onChanged(@Nullable List<Article> articles) {
                    if (articles != null) {
                        newsAdapter.setArticles(articles);
                        restoreRecyclerViewState();
                    } else {
                        newsAdapter.notifyDataSetChanged();
                        restoreRecyclerViewState();
                    }
                }
            });
        } else {
            Specification specs = new Specification();
            specs.setCategory("Business");
            viewModel.getNewsHeadlines(specs).observe(getActivity(), new Observer<List<Article>>() {
                @Override
                public void onChanged(@Nullable List<Article> articles) {
                    if (articles != null) {
                        newsAdapter.setArticles(articles);
                        restoreRecyclerViewState();
                    }
                }
            });
        }
    }

    @Override
    public void onNewsItemClicked(Article article) {
        Timber.d("Recieved article");
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.PARAM_ARTICLE, article);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        binding.rvNewsPosts.setLayoutAnimation(controller);
        binding.rvNewsPosts.scheduleLayoutAnimation();
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_up_animation, R.anim.fade_exit_transition);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (binding.rvNewsPosts.getLayoutManager() != null) {
            listState = binding.rvNewsPosts.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(PARAM_LIST_STATE, listState);
        }
    }

    private void restoreRecyclerViewState() {
        if (binding.rvNewsPosts.getLayoutManager() != null) {
            binding.rvNewsPosts.getLayoutManager().onRestoreInstanceState(listState);
        }
    }
}
