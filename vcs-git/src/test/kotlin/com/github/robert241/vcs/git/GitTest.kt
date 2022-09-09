package com.github.robert241.vcs.git

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GitTest {
    private val git = Git()
    @Test
    fun getHistory() {
        println(git.getHistory())
    }

    @Test
    fun getAllBranches() {
        println(git.getAllBranches())
    }

    @Test
    fun getCurrentBranch() {
        println(git.getCurrentBranch())
    }

    @Test
    fun switchBranch() {
    }

    @Test
    fun fetch() {
        git.fetch()
    }

    @Test
    fun pull() {
        git.pull()
    }
}