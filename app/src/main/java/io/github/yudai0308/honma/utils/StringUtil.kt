package io.github.yudai0308.honma.utils

class StringUtil {
    companion object {
        @JvmStatic
        fun divideWithComma(list: List<String>): String {
            val divider = ", "
            var returnVal = ""
            val listHasNoEmpty = list.filter { it.isNotEmpty() }
            val count = listHasNoEmpty.size
            listHasNoEmpty.forEachIndexed { index: Int, string: String ->
                val add = if (index == count - 1) string else string + divider
                returnVal += add
            }
            return returnVal
        }
    }
}