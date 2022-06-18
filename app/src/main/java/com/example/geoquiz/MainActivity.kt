package com.example.geoquiz

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"   //键值，用来保留实例状态.用于后面从bundle中取值
//private const val REQUEST_CODE_CHEAT = 0    //请求代码常量
private const val KEY_ANSWER = "KEY_ANSWER"     //键值，值放到onSaveInstanceState方法里面用Bundle对象保存起来
private const val EXTRA_CHEATE_NUMS = "com.example.geoquiz.cheat_nums"  //用于传递
private var Cheat_Nums = 3    //限制作弊3次

class MainActivity : AppCompatActivity() {

    //P89,首先给TextView和新Button添加视图属性，然后引用它们，并设置TextView显示当前集合索引所指向的地理知识问题
    private lateinit var trueButton: Button     //正确按钮
    private lateinit var falseButton: Button    //错误按钮
    private lateinit var nextButton: ImageButton     //下一题按钮
    private lateinit var prevButton: Button     //上一题按钮
    private lateinit var cheatButton: Button    //作弊按钮变量
    private lateinit var questionTextView: TextView
    private lateinit var apiLevel: TextView
    private lateinit var cheatNums: TextView
    //添加一个惰性初始化属性来保存与MainActivity关联的QuizViewModel实例
    //只在activity实例对象被创建后，才需要获取和保存QuizViewModel，也就是说，quizViewModel一次只应该赋一个值。
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    private val requestDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //   ?.  当对象为空什么都不做
            //   ?:  左边表达式不为空返回左边结果，否则返回右边
                //返回的数据显示，没有作弊false；返回ture，取出作弊值true
            quizViewModel.isCheater = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            Cheat_Nums = result.data?.getIntExtra(EXTRA_CHEATE_NUMS,0)!!
        }
/*
if (result.resultCode == RESULT_OK) {
            val data = result.data?.getStringExtra("data")
            // Handle data from SecondActivity
 */
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)  //引入布局资源ID传递;调用函数后，视图对象才会实例化到内存里

        //覆盖onSaveInstanceState(Bundle)函数
        //取出保存数据
        //最后，在onCreate(Bundle?)函数中确认是否成功获取该数值。如 果获取成功，就将它赋值给变量currentIndex；
        // 如果bundle里不存 在index键对应的值，或者Bundle对象是null，就将currentIndex的值设为0，
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        if (savedInstanceState != null) {
            //mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0)
            val answerList: IntArray? = savedInstanceState.getIntArray(KEY_ANSWER)
            for (i in quizViewModel.questionBank.indices) {
                quizViewModel.questionBank[i].answerd = answerList!![i] //将答题情况存储在question中
            }
        }

        val version : Int = Integer.valueOf(Build.VERSION.SDK_INT)

        /*代码清单4.4 ViewModelProviders弃用，见浏览器Android收藏夹
        val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)*/

        //在onCreate(...)里，将MainActivity和QuizViewModel实例关联起来。链式调用
        // 代码清单4.8删除
        //val quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
       // Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        //资源引用
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        apiLevel = findViewById(R.id.api_level)
        cheatNums = findViewById(R.id.cheat_nums)

        var x = 0
        apiLevel.setOnClickListener {
            x++
            if(x % 2 == 0) {
                apiLevel.text = resources.getString(R.string.api_level)
            } else {
               // val version = Integer.valueOf(Build.VERSION.SDK_INT)    //获取api版本
                apiLevel.text = "Api Level :$version"
        }
        }

        //2.8挑战练习，文字区域点击题目实现上下题跳转
        questionTextView.setOnClickListener {
          //  currentIndex = (currentIndex + 1) % questionBank.size
          //  quizViewModel.before()
            quizViewModel.moveToNext()
            updateQuestion()
        }

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }
/*代码清单4.9
            currentIndex--
            if (currentIndex < 0){
                currentIndex = questionBank.size-1
            }*/
        //2.9挑战练习，添加按钮实现返回上一题
        prevButton.setOnClickListener {
            quizViewModel.before()
            updateQuestion()
        }//2.9练习

        cheatButton.setOnClickListener {

            //监听器代码中，创建包含CheatActivity类的Intent实例，然后将其传入startActivity(Intent)函数
            //传入Intent构造函数的Class类型参数告诉ActivityManager应 该启动哪个activity。
            // Context参数告诉ActivityManager在哪里可以找到它
            // Start CheatActivity
            //val intent = Intent(this, CheatActivity::class.java)
            //startActivity(intent)

            //用extra启动CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer  //调用ViewModel类函数中的当前问题答案
            //传递答案给CheatActivity
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            intent.putExtra(EXTRA_CHEATE_NUMS, Cheat_Nums)

/*代码清单7-1　添加动画特效代码（MainActivity.kt）
使用ActivityOptions类来定制该如何启动activity。调用makeClipRevealAnimation(...)可以让
CheatActivity出现时带动画效果。传入makeClipRevealAnimation(...)中的参数值指定了视图动画对
象（这里是指CHEAT!按钮）、显示新activity位置的x 和y 坐标（相对于动画源对象），以及新activity的初始高宽值。
请注意，这里直接使用了命名lambda值参view，而不是默认的it名字。在设置点击监听器的上下文中，lambda值参表示被点击的视图。
虽然不一定需要明确命名，但代码可读性提高了。对于这种使用值参的lambda体，阅读代码的新人无法很快知道值参的含义，因此推荐做好命名。
最后，调用options.toBundle()把ActivityOptions信息打包到Bundle对象里，然后传给startActivityForResult(...)。
随后，ActivityManager就知道该如何展现你的activity了。

Android直到SDK API 23级才加入makeClipRevealAnimation(...)函数。因此，这段代码在低版
本（API 22级或更低）设备上运行时会让应用崩溃。
            val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
            startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
*/
/*一种办法是提升SDK最低版本到23
比较好的做法是将高API级别代码置于检查Android设备版本的条件语句中
Build.VERSION.SDK_INT常量代表了Android设备的版本号。可将该常量同代表Marshmallow版本的常量进行比较。
检查设备的编译版本
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
 val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0,view.width, view.height)
 startActivityForResult(intent, REQUEST_CODE_CHEAT,options.toBundle())
 } else {
 startActivityForResult(intent, REQUEST_CODE_CHEAT)
 }
*/
            requestDataLauncher.launch(intent)

            //startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        //监听器的作用是让集合索引递增并相应地更新TextView的文本内容
        nextButton.setOnClickListener {
            //currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()  //P147,注释上一行.更新题干，移动到下一题号
            updateQuestion()
            answerLength++

            if (answerLength == quizViewModel.questionBank.size.toDouble()){
                val i =correctAnswered/quizViewModel.questionBank.size
                val score="%.2f".format(i*100)
                Toast.makeText(this, "score=$score.",Toast.LENGTH_SHORT).show()
            }
        }

        updateQuestion()    //调用是为了初始化设置activity视图中的文本
    }

/*  在MainActivity.kt中新增一个成员变量来保存CheatActivity回传的值，然后覆盖
    onActivityResult(...)函数获取它。别忘了检查请求代码和返回代码是否符合预期。

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode != REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
*/
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        if (Cheat_Nums == 0) {
            cheatButton.isEnabled = false
        }
    cheatNums.text = "Remaining number of cheating:$Cheat_Nums"

    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }


    //覆盖onSaveInstanceState(Bundle)函数，以刚才新增的常量值作为键，将currentIndex变量值保存到bundle中
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)    //保存整型数据
        val answeredList = IntArray(quizViewModel.questionBank.size)
        for (i in quizViewModel.questionBank.indices){
            answeredList[i] = quizViewModel.questionBank[i].answerd
        }
        savedInstanceState.putIntArray(KEY_ANSWER, answeredList) //保存作答状态

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    //创建ButtonEnabled函数，将答过的题目禁止答
    private fun buttonEnabled(){
        if (quizViewModel.currentQuestionAnswerd != 0){
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        } else {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }

    }

    private fun updateQuestion() {
        // val questionTextResId = questionBank[currentIndex].textResId
        //P147,注释上一行.更新题干
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        buttonEnabled() //更新题目，判断按钮状态
    }


    //当前，GeoQuiz应用认为所有问题的答案都是true，下面着手修正这个逻辑错误。
    //同样，为避免代码重复，我们将解决方案封装在一个私有函数里。
    //该函数接受布尔类型的变量参数，判别用户点击了TRUE还是FALSE按钮。
    //然后，将用户的答案同当前Question对象中的答案做比较，判断正误，并生成一个toast消息反馈给用户。
    private var correctAnswered : Double = 0.0
    private var answerLength : Double = 0.0
    private fun checkAnswer(userAnswer: Boolean) {
        //val correctAnswer = questionBank[currentIndex].answer
        //P147,注释上一行.更新题干
        val correctAnswer: Boolean = quizViewModel.currentQuestionAnswer
        /*
        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }*/

        //接119行代码。
        // 最后，修改MainActivity中的checkAnswer(Boolean)函 数，确认用户是否偷看答案并作出相应的反应。
        // 基于isCheater变量值改变toast消息的做法
        /*val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }*/

        val messageResId: Int
        if (quizViewModel.isCheater){
            messageResId = R.string.judgment_toast
        } else if (userAnswer == correctAnswer){
            quizViewModel.questionBank[quizViewModel.currentIndex].answerd = 1
            messageResId = R.string.correct_toast
            buttonEnabled()
            correctAnswered++
        } else {
            quizViewModel.questionBank[quizViewModel.currentIndex].answerd = -1
            messageResId = R.string.incorrect_toast
            buttonEnabled()
        }
        //静态函数。该函数会创建并配置Toast对象。
        //Toast类必须借助Context才能找到并使用字符串资源ID
        //1.11挑战练习，在顶部显示弹窗消息
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.TOP, 0, 0)
        }.show()
    }

}

/*
运行GeoQuiz应用。点击CHEAT!按钮，然后在作弊界面点击SHOW
ANSWER按钮。偷看答案后，点击回退键。在回答当前问题时，你
会看到作弊警告消息弹出。
不再作弊，继续答下一题会是什么情况呢？依然被判作弊！这就
有点严苛了。如果想得到更合情理的评判，请动手完成6.6节的挑
战练习，完善作弊评判逻辑。*/