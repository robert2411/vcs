package com.github.robert241.vcs

interface VCS {

    fun getHistory(): VCSHistory
    fun getAllBranches(): List<String>
    fun getCurrentBranch(): String


    fun switchBranch(branch: String)
    fun fetch()
    fun pull()

    fun revertChanges()

    fun addAllFiles()
    fun commit(commitMessage: String)
    fun push(pushTags: Boolean)

}