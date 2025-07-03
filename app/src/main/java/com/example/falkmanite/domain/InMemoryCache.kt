package com.example.falkmanite.domain

interface InMemoryCache<T> {
    fun read() : T
    fun save(data: T) : T
}
