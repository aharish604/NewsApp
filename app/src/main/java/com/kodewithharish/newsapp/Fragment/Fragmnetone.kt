package com.kodewithharish.newsapp.Fragment

import NewsViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodewithharish.newsapp.NewsActivity
import com.kodewithharish.newsapp.R
import com.kodewithharish.newsapp.adapter.NewsAdapter
import com.kodewithharish.newsapp.utils.Constans.Companion.Query_Page_Size
import com.kodewithharish.newsapp.utils.Resourcee

class Fragmnetone: Fragment() {

    var mcontext: Context?=null
    lateinit var recyclerview:RecyclerView
    lateinit var paginationProgressBar:ProgressBar

    lateinit var  viewModel: NewsViewModel
    lateinit var  newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"

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
            R.layout.fragmnet_one,container,false
        )

        viewModel = (activity as NewsActivity).viewModel
        recyclerview=view.findViewById(R.id.rvBreakingNews)
        paginationProgressBar=view.findViewById(R.id.paginationProgressBar)


        setupRecylerView()
        newsAdapter.setOnItemClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(it.url)
            startActivity(openURL)
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resourcee.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Query_Page_Size + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages

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

    val scrollListiner  = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private  fun  setupRecylerView(){
        newsAdapter = NewsAdapter()
        recyclerview.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@Fragmnetone.scrollListiner)

        }
    }

}