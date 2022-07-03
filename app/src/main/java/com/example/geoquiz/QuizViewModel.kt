package com.example.geoquiz

import androidx.lifecycle.ViewModel

//private const val TAG = "QuizViewModel"
class QuizViewModel : ViewModel() {

    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true))

    var currentIndex = 0    //变量
    var cheatNumbs = 3
    //新属性来保存CheatActivity传回的值。用户是否作弊属于UI状态数据。
    //UI状态数据保存在ViewModel里不会像activity那样因设备配置改变被销毁而丢失数据
    //var isCheater = false  //记录是否作弊

        //MainActivity会调用添加到QuizViewModel里的函数和计算属性，而不是直接访问questionBank。另外，init和
        //onCleared()日志记录代码没用了，顺手删除它们。接着，在QuizViewModel里，添加地理知识问题出题函数，以及返
        //回当前题干内容和答案的计算属性
    val currentQuestionAnswer: Boolean  //当前问题答案
        get() = questionBank[currentIndex].answer   //函数获取调用对错答案，不直接访问数据
    val currentQuestionText: Int        //当前问题
        get() = questionBank[currentIndex].textResId    //函数获取调用题目的资源ID，不直接访问数据
    val currentQuestionAnswered: Int   //当前问题是否作答
        get() = questionBank[currentIndex].answerd
    var currentQuestioncheated: Boolean = false //当前问题是否作弊
        get() = questionBank[currentIndex].isCheated


    fun before() {
        currentIndex--
        if (currentIndex < 0){
            currentIndex = questionBank.size-1
        }
    }
    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size   //移动到下一题号
    }


    /*P138,添加init代码块并覆盖onCleared()函数，另外再调用日志函数记录QuizViewModel实例的创建和销毁。
    init {
        Log.d(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
*/





}