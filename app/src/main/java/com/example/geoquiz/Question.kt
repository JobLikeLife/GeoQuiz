package com.example.geoquiz

import androidx.annotation.StringRes

/*class Question {
}
新增一个名为Question的数据类。一个实例代表一道题目。
然后再创建一个Question对象集合交由MainActivity管理。
Question类中封装的数据有两部分：问题文本和问题答案（true或false）。
增加：是否作答，控制按钮状态
*/
data class Question(@StringRes val textResId: Int, val answer: Boolean, var answerd: Int = 0, var isCheated: Boolean = false)