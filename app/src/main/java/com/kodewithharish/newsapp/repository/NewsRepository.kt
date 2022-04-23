package com.kodewithharish.newsapp.repository

import com.kodewithharish.newsapp.API.RetrofitInstance
import com.kodewithharish.newsapp.database.ArticleDatabase
import com.kodewithharish.newsapp.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countyCode: String, pageNumber: Int)=
        RetrofitInstance.getInstance.getBreakingNews(countyCode,pageNumber)

    suspend fun  searchNews(searchQery:String, pageNumber: Int)=
        RetrofitInstance.getInstance.searchForNews(searchQery,pageNumber)

    suspend fun  insert (article : Article) = db.getArticleDao().upsert(article)

     fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun  deleteArticle(article: Article) = db.getArticleDao().deleteArtcile(article)
}