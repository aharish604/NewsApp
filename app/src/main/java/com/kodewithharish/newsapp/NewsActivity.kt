package com.kodewithharish.newsapp

import NewsViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kodewithharish.newsapp.adapter.NewsAdapter
import com.kodewithharish.newsapp.database.ArticleDatabase
import com.kodewithharish.newsapp.fragmentadapter.Viewpager2Adapter
import com.kodewithharish.newsapp.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter
    private lateinit var viewpager2: ViewPager2
    private lateinit var bottombar: BottomNavigationView
    lateinit var adapter_pager: Viewpager2Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository = NewsRepository(ArticleDatabase.invoke(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        viewpager2=findViewById(R.id.viewpager)
        bottombar=findViewById(R.id.bottom_navigation)
        viewpager2.isUserInputEnabled = false

        viewpager2.adapter= Viewpager2Adapter(supportFragmentManager,lifecycle)

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    viewpager2.setCurrentItem(0, true)
                    true
                }
                R.id.page_2 -> {
                    viewpager2.setCurrentItem(1, true)
                    true
                }
                else -> false
            }
        }

        // Listen bottom navigation tabs change
        bottombar.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.page_1 -> {
                    viewpager2.setCurrentItem(0, true)
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.page_2 -> {
                    viewpager2.setCurrentItem(1, true)
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }


        }

    }





