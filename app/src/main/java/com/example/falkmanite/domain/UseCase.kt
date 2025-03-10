package com.example.falkmanite.domain

interface UseCase<T, R> {
    operator fun invoke(data: T) : R
}

interface UnitUseCase<R> {
    operator fun invoke() : R
}

interface CombinedUseCase<T, R> : UseCase<T, R>, UnitUseCase<R>


