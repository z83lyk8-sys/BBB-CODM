package com.bbb.gaming

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Canvas
import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {
    private val bg=Color.rgb(8,8,8); private val panel=Color.rgb(27,27,27); private val red=Color.rgb(255,48,48)
    private lateinit var prefs: SharedPreferences
    private var size=24; private var gap=8; private var crossColor=Color.RED
    private var running=false; private var started=0L; private var elapsed=0L; private var clock:TextView?=null
    private val handler=android.os.Handler()
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState); requestedOrientation=android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        prefs=getSharedPreferences("bbb",MODE_PRIVATE); size=prefs.getInt("size",24); gap=prefs.getInt("gap",8); crossColor=prefs.getInt("color",red); elapsed=prefs.getLong("elapsed",0); home() }
    private fun txt(s:String,n:Float=15f,c:Int=Color.WHITE,b:Boolean=false)=TextView(this).apply{text=s;textSize=n;setTextColor(c);if(b) setTypeface(null,1);setPadding(0,4,0,4)}
    private fun btn(s:String,f:()->Unit)=Button(this).apply{text=s;isAllCaps=false;setTextColor(-1);setBackgroundColor(panel);setOnClickListener{f()}}
    private fun page(title:String,sub:String):LinearLayout{
        val l=LinearLayout(this).apply{orientation=1;setPadding(20,12,20,14);setBackgroundColor(bg)}
        l.addView(txt("♛ BBB",28f,red,true));l.addView(txt(title,22f,-1,true));l.addView(txt(sub,13f,Color.LTGRAY))
        val nav=LinearLayout(this).apply{orientation=0}
        listOf("الرئيسية","الأدوات","الحساسية","الجهاز","حول").forEachIndexed{i,s->nav.addView(btn(s){when(i){0->home();1->tools();2->profiles();3->device();else->about()}},LinearLayout.LayoutParams(0,45,1f).apply{setMargins(2,3,2,8)})}
        l.addView(nav);return l
    }
    private fun card(l:LinearLayout,h:String,d:String){val b=LinearLayout(this).apply{orientation=1;setPadding(14,10,14,10);setBackgroundColor(panel)}
        b.addView(txt(h,17f,red,true));b.addView(txt(d,14f));l.addView(b,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,5,0,7)})}
    private fun show(l:LinearLayout){setContentView(ScrollView(this).apply{isFillViewport=true;addView(l)})}
    private fun home(){val l=page("لوحة التحكم","CODM • PUBG Mobile • Gaming tools")
        card(l,"BBB GAMING EDITION ULTIMATE","مركز أدوات مساعدة مستقل بواجهة أفقية سوداء وحمراء.")
        card(l,"Crosshair Studio","تخصيص شكل العلامة ومعاينتها داخل التطبيق.")
        card(l,"Training Lab","مؤقت لجلسات التدريب خارج اللعبة.")
        card(l,"Sensitivity Profiles","حفظ ملاحظات وملفات الحساسية محلياً.")
        card(l,"Touch Notes & Device Monitor","ملاحظات التحكم ومعلومات الجهاز والذاكرة الخاصة بهذا التطبيق.")
        card(l,"شفافية","لا يوجد غش أو حقن أو تجاوز حماية. لا نعرض FPS للعبة بأرقام وهمية.")
        show(l)}
    private fun tools(){val l=page("مركز الأدوات","أدوات مستقلة")
        l.addView(btn("فتح Crosshair Studio"){crosshair()})
        val note=EditText(this).apply{hint="ملاحظات التحكم والتدريب";setTextColor(-1);setHintTextColor(Color.GRAY);setBackgroundColor(panel);minLines=3;setText(prefs.getString("notes",""))}
        l.addView(note);l.addView(btn("حفظ الملاحظات"){prefs.edit().putString("notes",note.text.toString()).apply();toast("تم الحفظ")})
        l.addView(btn("فتح مؤقت التدريب"){training()})
        card(l,"تنبيه","المعاينة والملاحظات لا تتصل باللعبة ولا تعدّل ملفاتها.");show(l)}
    private fun crosshair(){val l=page("Crosshair Studio","المعاينة داخل التطبيق فقط")
        l.addView(FrameLayout(this).apply{setBackgroundColor(Color.DKGRAY);addView(CrossView(this@MainActivity),FrameLayout.LayoutParams(-1,210))},LinearLayout.LayoutParams(-1,210))
        l.addView(txt("الحجم: $size"));l.addView(SeekBar(this).apply{max=48;progress=size-8;setOnSeekBarChangeListener(listener{size=it+8;prefs.edit().putInt("size",size).apply();crosshair()})})
        l.addView(txt("الفراغ: $gap"));l.addView(SeekBar(this).apply{max=24;progress=gap;setOnSeekBarChangeListener(listener{gap=it;prefs.edit().putInt("gap",gap).apply();crosshair()})})
        l.addView(btn("أحمر"){color(red)});l.addView(btn("أخضر"){color(Color.GREEN)});l.addView(btn("أبيض"){color(Color.WHITE)});show(l)}
    private fun color(c:Int){crossColor=c;prefs.edit().putInt("color",c).apply();crosshair()}
    private fun listener(f:(Int)->Unit)=object:SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(s:SeekBar?,p:Int,u:Boolean){if(u)f(p)}
        override fun onStartTrackingTouch(s:SeekBar?){};override fun onStopTrackingTouch(s:SeekBar?){}}
    private fun training(){val l=page("Training Lab","مؤقت مستقل")
        clock=txt(fmt(elapsed),40f,red,true).apply{gravity=Gravity.CENTER};l.addView(clock,LinearLayout.LayoutParams(-1,85))
        l.addView(btn("بدء / إيقاف"){if(running){elapsed=SystemClock.elapsedRealtime()-started;running=false;prefs.edit().putLong("elapsed",elapsed).apply()}else{started=SystemClock.elapsedRealtime()-elapsed;running=true;tick()}})
        l.addView(btn("إعادة ضبط"){running=false;elapsed=0;prefs.edit().putLong("elapsed",0).apply();clock?.text=fmt(0)})
        card(l,"ملاحظة","المؤقت لا يحلل التصويب ولا يتصل باللعبة.");show(l);if(running)tick()}
    private fun tick(){if(!running)return;elapsed=SystemClock.elapsedRealtime()-started;clock?.text=fmt(elapsed);handler.postDelayed({tick()},500)}
    private fun fmt(ms:Long)=String.format(Locale.US,"%02d:%02d",ms/60000,(ms/1000)%60)
    private fun profiles(){val l=page("Sensitivity Profiles","حفظ محلي للقيم التي تدخلها بنفسك")
        val game=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,listOf("PUBG Mobile","Call of Duty Mobile"))}
        val name=EditText(this).apply{hint="اسم الملف";setTextColor(-1);setHintTextColor(Color.GRAY);setBackgroundColor(panel)}
        val values=EditText(this).apply{hint="قيم الحساسية وملاحظاتك";setTextColor(-1);setHintTextColor(Color.GRAY);setBackgroundColor(panel);minLines=2}
        l.addView(game);l.addView(name);l.addView(values);l.addView(btn("حفظ الملف"){val n=name.text.toString().trim();if(n.isEmpty())toast("اكتب اسم الملف")else{prefs.edit().putString("profile_$n","${game.selectedItem}\n${values.text}").apply();toast("تم الحفظ محلياً");profiles()}})
        prefs.all.filterKeys{it.startsWith("profile_")}.forEach{(k,v)->card(l,k.removePrefix("profile_"),v.toString())};show(l)}
    private fun device(){val l=page("معلومات الجهاز","بيانات متاحة لهذا التطبيق")
        val r=Runtime.getRuntime();card(l,"الجهاز","${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
        card(l,"Android",android.os.Build.VERSION.RELEASE?: "غير معروف")
        card(l,"ذاكرة التطبيق","${(r.totalMemory()-r.freeMemory())/1048576} MB مستخدمة تقريباً من حد ${r.maxMemory()/1048576} MB")
        card(l,"FPS والحرارة","لا يقرأ التطبيق FPS الفعلي للعبة أو حرارة مكوناتها مباشرة.");show(l)}
    private fun about(){val l=page("حول BBB","الإصدار 3.0 Ultimate")
        card(l,"المنفذ","واجهة أفقية، Crosshair محلي، مؤقت تدريب، ملفات حساسية وملاحظات محفوظة، ومعلومات الجهاز.")
        card(l,"غير متوفر","لا Aimbot أو ESP أو Color Tracker داخل اللعبة أو Overlay أو حقن أو تجاوز Anti-Cheat أو تعديل FOV/FPS.")
        card(l,"الخصوصية","لا يطلب صلاحيات خاصة ولا يرسل بيانات.");show(l)}
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    private inner class CrossView(c:Context):View(c){val p=Paint(3).apply{strokeWidth=5f;strokeCap=Paint.Cap.ROUND}
        override fun onDraw(c:Canvas){super.onDraw(c);p.color=crossColor;val x=width/2f;val y=height/2f;val z=size.toFloat();val g=gap.toFloat()
            c.drawLine(x-z,y,x-g,y,p);c.drawLine(x+g,y,x+z,y,p);c.drawLine(x,y-z,x,y-g,p);c.drawLine(x,y+g,x,y+z,p);c.drawCircle(x,y,3f,p)}}
}
