package com.github.robert241.vcs

import java.time.LocalDate

data class Commit(val hash: String, val rawMessage: String, val author: String, val date: LocalDate = LocalDate.now(), val tags: List<String> = emptyList())

//The oldest vcs commit should have index 0 in the commit list
data class VCSHistory(var commits: List<Commit>)