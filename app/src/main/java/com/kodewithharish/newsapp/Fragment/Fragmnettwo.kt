package com.kodewithharish.newsapp.Fragment

import NewsViewModel
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodewithharish.newsapp.NewsActivity
import com.kodewithharish.newsapp.R
import com.kodewithharish.newsapp.adapter.NewsAdapter
import com.kodewithharish.newsapp.utils.Constans
import com.kodewithharish.newsapp.utils.Constans.Companion.Search_News_Delay
import com.kodewithharish.newsapp.utils.Resourcee
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Fragmnettwo:Fragment() {

    var mcontext: Context?=null
    lateinit var etSearch:EditText
    lateinit var rvSearchNews:RecyclerView
    lateinit var paginationProgressBar:ProgressBar
    lateinit var  viewModel: NewsViewModel
    lateinit var  newsAdapter: NewsAdapter
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mcontext=context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(
            R.layout.fragmnet_two,container,false
        )

        etSearch=view.findViewById(R.id.etSearch)
        rvSearchNews=view.findViewById(R.id.rvSearchNews)
        paginationProgressBar=view.findViewById(R.id.paginationProgressBar)

        viewModel = (activity as NewsActivity).viewModel
        setupRecylerView()
        newsAdapter.setOnItemClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(it.url)
            startActivity(openURL)
                   }

        var job: Job? = null
        etSearch.addTextChangedListener {editable ->
            job?.cancel()
            job= MainScope().launch {
                delay(Search_News_Delay)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resourcee.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                        val totalPages = newsResponse.totalResults / Constans.Query_Page_Size + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                    }
                }
                is Resourcee.Error ->{
                    hideProgressBar()
                    response.message?.let {message->
                        Log.e(TAG,"Error: $message")
                    }
                }
                is Resourcee.Loading -> {
                    showProgressBar()
                }
            }
        })



        return view

    }

    private fun  showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = false
    }

    private fun  hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = true
    }
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constans.Query_Page_Size
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            } else {
                rvSearchNews.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private  fun  setupRecylerView(){
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@Fragmnettwo.scrollListener)

        }
    }
}