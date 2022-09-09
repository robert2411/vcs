package com.github.robert241.vcs.git

import com.github.robert241.vcs.Commit
import com.github.robert241.vcs.VCS
import com.github.robert241.vcs.VCSException
import com.github.robert241.vcs.VCSHistory
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class Git: VCS {
    private val commitRegex = Regex("\"\\[(\\w+)]\\s+\\[((?:\\w|\\s)+)]\\s+\\[((?:\\w|\\s|-)+)]\\s+\\[((?:.*?)+)]\\s\\[((?:.*?)*)]\"")
    private val tagRegex = Regex("tag:\\s(.*?)(?:,|\\)|\$)")

    private val branchRegex = Regex("((?:\\w|\\/)+)")

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    override fun getHistory(): VCSHistory {
        val rawLog = "git log --pretty=format:\"[%h]%x09[%an]%x09[%ad]%x09[%s]%x09[%d]\" --date=short".runCommand().lines()
        return VCSHistory(rawLog.map { r -> logLineToCommit(r) }.asReversed())
    }

    override fun getAllBranches(): List<String> {
        return "git branch -a"
            .runCommand()
            .lines()
            .filter { b -> b.isNotBlank() }
            .map { b -> cleanBranch(b) }
    }

    private fun cleanBranch(rawBranch: String): String{
        val match = branchRegex.find(rawBranch)!!
        val (branch) = match.destructured
        return branch
    }

    override fun getCurrentBranch(): String {
        return "git branch --show-current".runCommand()
    }

    override fun switchBranch(branch: String) {
        "git checkout $branch".runCommand()
    }

    override fun fetch() {
        "git fetch --all".runCommand()
    }

    override fun pull() {
        "git pull".runCommand()
    }

    private fun logLineToCommit(logLine: String): Commit {
        val match = commitRegex.find(logLine)!!
        val (hash, author, rawDate, rawMessage, rawTags) = match.destructured
        val date = LocalDate.parse(rawDate, dateTimeFormatter)

        val tags = rawTagsToTag(rawTags)
        return Commit(hash, rawMessage, author,date, tags)
    }

    private fun rawTagsToTag(rawTags: String): List<String>{
        if (rawTags.isEmpty()){
            return emptyList()
        }

        val out = mutableListOf<String>()
        var match = tagRegex.find(rawTags)
        while (match != null){
            val (tag) = match.destructured
            out.add(tag)
            match = match.next()
        }
        return out
    }
}

private fun String.runCommand(timeout: Long = 120, workingDir: File = File(System.getProperty("user.dir"))): String {
    return try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(timeout, TimeUnit.MINUTES)
        proc.inputStream.bufferedReader().readText()
    } catch(e: Exception) {
        throw VCSException(cause = e)
    }
}