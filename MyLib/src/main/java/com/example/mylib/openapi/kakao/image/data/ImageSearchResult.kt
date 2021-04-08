package com.example.mylib.openapi.kakao.image.data

import com.example.mylib.openapi.kakao.image.data.Document
import com.example.mylib.openapi.kakao.image.data.Meta

data class ImageSearchResult(
    val documents: List<Document>,
    val meta: Meta
)