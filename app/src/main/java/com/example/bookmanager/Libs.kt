package com.example.bookmanager

class Libs {
    companion object {
        fun listToString(list: List<String>, divider: String = ", "): String {
            var returnVal = ""
            val count = list.size
            list.forEachIndexed { index: Int, string: String ->
                val add = if (index == count - 1) string else string + divider
                returnVal += add
            }
            return returnVal
        }
    }
}