package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

private const val TAG = "CheatActivity"
const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"   //要回传给MainActivityextra的常量键
//activity可能启动自不同的地方，所以，应该在获取和使用extra信息 的activity那里，为它定义键
private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"   //为extra数据信息新增键值对中的键
const val EXTRA_CHEATE_NUMS = "com.example.geoquiz.cheat_nums"  //用于传递


class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView   //显示正确答案变量
    private lateinit var showAnswerButton: Button   //按钮变量
    private var answerIsTrue = false                //成员变量
    private var cheatNums: Int = 0     //默认限制作弊3次

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)     //引入布局资源ID传递

        //在onCreate(Bundle?)里，从目标newIntent中extra里取值，存入成员变量中
        //P198,请注意，Activity.getIntent()函数返回了由startActivity(Intent)函数转发的Intent对象。
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)  //将newIntent传递过来的字符串赋值给成员变量,第二个参数默认为false
        Log.d(TAG, "$answerIsTrue")
        cheatNums = intent.getIntExtra(EXTRA_CHEATE_NUMS,3)
        Log.d(TAG, "$cheatNums")
        //实现点击SHOW ANSWER按钮后获取答案并将其显示在TextView上
        answerTextView = findViewById(R.id.answer_text_view)    //资源引用，用来显示作弊后的答案
        showAnswerButton = findViewById(R.id.show_answer_button)    //资源引用

        //实现点击SHOW ANSWER按钮后获取答案并将其显示在TextView上,返回通知作弊
        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            if(cheatNums >0){
            answerTextView.setText(answerText)
            showAnswerButton.isEnabled = false
                --cheatNums
                Log.d(TAG, "er$cheatNums")
            }
            setAnswerShownResult(true, cheatNums)  //设置作弊显示通知结果true，作弊了
        }
    }

    //在GeoQuiz应用中，数据信息需要回传给MainActivity。因 此，我们需要创建一个Intent，附加上extra信息后，调用
    //Activity.setResult(Int, Intent)函数将信息回传给MainActivity。
    //如代码清单6-14所示，在CheatActivity代码中，为extra的键增加常量，再创建一个私有函数，用来创建intent、附加extra并
    //设置结果值。然后在SHOW ANSWER按钮的监听器代码中调用它
    //用户点击SHOW ANSWER按钮时，CheatActivity调用setResult(Int, Intent)函数将结果代码以及intent打包。
    //然后，在用户按回退键回到MainActivity时，ActivityManager调用父activity的以下函数：
    //onActivityResult(requestCode: Int, resultCode: Int, data:Intent)
    //该函数的参数来自MainActivity的原始请求代码以及传入setResult(Int, Intent)函数的结果代码和intent。
    //最后，在MainActivity里覆盖onActivityResult(Int,Int, Intent)函数来处理返回结果。
    private fun setAnswerShownResult(isAnswerShown: Boolean, cheatNums: Int) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
            putExtra(EXTRA_CHEATE_NUMS, cheatNums)
        }
        setResult(Activity.RESULT_OK, data) //返回处理结果，把带有数据的intent传递回去，结果是true作弊了
    }

    //现在，可以返回到MainActivity，将extra附加到intent上。不过我们有个更好的实现方法。
    //对于CheatActivity处理extra信息的实现细节，MainActivity和应用的其他代码无须知道。
    //因此，我们可转而在newIntent(...)函数中封装这些逻辑。
    //在CheatActivity中，创建newIntent(...)函数，把它放在一个companion对象里.
    //使用新建的newIntent(...)函数可以正确创建Intent，它配置有 CheatActivity需要的extra。
    // answerIsTrue布尔值以 EXTRA_ANSWER_IS_TRUE常量放入intent以供解析。稍后，我们会取出这个值。
    //即使没有类实例，使用companion对象也可以调用类函数，这点和Java里的静态函数类似。
    companion object {
        fun newIntent(packageContext: Context, answerIsTrue:
        Boolean, cheatNums: Int): Intent {
            return Intent(packageContext,
                CheatActivity::class.java).apply {
                //使用intent.extra方法将MainActivity中的题目答案传递给CheatActivity
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_CHEATE_NUMS, cheatNums)
            }
        }
    }

}