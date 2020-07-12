package com.example.ashikablurfacehw3

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.FaceDetector
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.imageView as imageView1

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.google.android.gms.vision.face.Landmark
import android.widget.TextView



class MainActivity : AppCompatActivity(), SensorEventListener {

    var picBitmap: Bitmap? = null

    //var faces: SparseArray<Face>? = null

    lateinit var imageView: ImageView
    lateinit var fdButton: Button
    lateinit var button2: Button
    lateinit var button3: Button
    lateinit var textView: TextView
    private lateinit var msensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    private var lastX: Float = 0.toFloat()
    var lastY: Float = 0.toFloat()
    var lastZ: Float = 0.toFloat()

    private var deltaXMax = 0f
    private var deltaYMax = 0f
    private var deltaZMax = 0f

    private var deltaX = 0f
    private var deltaY = 0f
    private var deltaZ = 0f

    private var blurThreshold = 0f

    private val Smile_Prob = 0.4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById<ImageView>(R.id.imageView)
        fdButton = findViewById<Button>(R.id.fdbutton)
        button2 = findViewById<Button>(R.id.button2)
        textView = findViewById<TextView>(R.id.textView)
        button3 = findViewById<Button>(R.id.button3)

        fdButton.setOnClickListener {
            blurFace()
        }

        button2.setOnClickListener {
          showOriginalImage()
        }

        button3.setOnClickListener {
            detectFace()
        }

        this.msensorManager= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.mAccelerometer= msensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        this.blurThreshold = mAccelerometer.maximumRange/2
        Log.d("blurThreshold", "${this.blurThreshold}")
        msensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
        showOriginalImage()
    }

    private fun showOriginalImage(){

        val bitMapOptions = BitmapFactory.Options()
        bitMapOptions.inMutable = true
        picBitmap = BitmapFactory.decodeResource(resources,R.drawable.pic1, bitMapOptions)
        imageView.setImageBitmap(picBitmap)

    }

    private fun detectFace(){
        Log.d("detect", "detecting face")
        val rectPaint = Paint()
        rectPaint.strokeWidth = 5F
        rectPaint.color = Color.YELLOW
        rectPaint.style = Paint.Style.STROKE


        val myBitmap = picBitmap
        Log.d("width", "$myBitmap.width")
        Log.d("height", "$myBitmap.height")
        val tempBitmap = Bitmap.createBitmap(myBitmap!!.width, myBitmap.height, Bitmap.Config.ARGB_4444)

        val myCanvas = Canvas(tempBitmap)
        myCanvas.drawBitmap(myBitmap, 0F, 0F, null)

        val detector = com.google.android.gms.vision.face.FaceDetector.Builder(applicationContext)
            .setTrackingEnabled(false)
            .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
            .setMode(com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE)
            .build()

        if(!detector.isOperational){
            AlertDialog.Builder(this)
                .setMessage("Ran into issues while setting up the face detector")
                .show()
            return
        }


        val frame = Frame.Builder().setBitmap(myBitmap).build()

        val faces = detector.detect(frame)
        Log.d("mylog", "faces detected from the api $faces")

        /* val viewWidth = myCanvas.width.toDouble()
         val viewHeight = myCanvas.height.toDouble()
         val imageWidth = myBitmap!!.width.toDouble()
         val imageHeight = myBitmap!!.height.toDouble()
         val scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight)*/

        for(i in 0 until faces!!.size()) {
            val face = faces!!.valueAt(i)

            var mBitmap_blurred: Bitmap? = null
            for(landmark in face.landmarks){
                val cx = (landmark.position.x)
                val cy = (landmark.position.y)
                myCanvas.drawCircle(cx, cy, 1f, rectPaint)
                //val curBitmap = Bitmap.createBitmap(myBitmap!!.width, myBitmap.height, Bitmap.Config.ARGB_4444)
                //Blurring the image
                //Bitmap.createBitmap()


            }





        }
        imageView.setImageDrawable(BitmapDrawable(resources, tempBitmap))

        detector.release()


    }

    private fun blurFace(){
        Log.d("blur", "blurring face")
        val rectPaint = Paint()
        rectPaint.strokeWidth = 5F
        rectPaint.color = Color.YELLOW
        rectPaint.style = Paint.Style.STROKE


        val myBitmap = picBitmap
        Log.d("width", "$myBitmap.width")
        Log.d("height", "$myBitmap.height")
        val tempBitmap = Bitmap.createBitmap(myBitmap!!.width, myBitmap.height, Bitmap.Config.ARGB_4444)

        val myCanvas = Canvas(tempBitmap)
        myCanvas.drawBitmap(myBitmap, 0F, 0F, null)

        val detector = com.google.android.gms.vision.face.FaceDetector.Builder(applicationContext)
            .setTrackingEnabled(false)
            .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
            .setMode(com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE)
            .build()

        if(!detector.isOperational){
            AlertDialog.Builder(this)
                .setMessage("Ran into issues while setting up the face detector")
                .show()
            return
        }


        val frame = Frame.Builder().setBitmap(myBitmap).build()

        val faces = detector.detect(frame)
        Log.d("mylog", "faces detected from the api $faces")

       /* val viewWidth = myCanvas.width.toDouble()
        val viewHeight = myCanvas.height.toDouble()
        val imageWidth = myBitmap!!.width.toDouble()
        val imageHeight = myBitmap!!.height.toDouble()
        val scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight)*/

        for(i in 0 until faces!!.size()) {
            val face = faces!!.valueAt(i)
            var smiling = face.getIsSmilingProbability()
            if(smiling < 0)
            {
                smiling = smiling *-1
            }
            if( smiling  > Smile_Prob)
            {
                textView.setText("Person is smiling")
            }
            else
            {
                textView.setText("Person is not smiling")
            }

            var mBitmap_blurred: Bitmap? = null
            for(landmark in face.landmarks){
                val cx = (landmark.position.x)
                val cy = (landmark.position.y)
                //bitmapBlur(myBitmap, scale.toFloat(), 10)
                //myCanvas.drawCircle(cx, cy, 1f, rectPaint)
                //val curBitmap = Bitmap.createBitmap(myBitmap!!.width, myBitmap.height, Bitmap.Config.ARGB_4444)
                //Blurring the image
                //Bitmap.createBitmap()

                /*if(landmark.type==Landmark.NOSE_BASE){
                    val faceBitmap = Bitmap.createBitmap(
                        myBitmap,
                        landmark.position.x.toInt(),
                        landmark.position.y.toInt(),
                        2,
                        2
                    )
                    //val destBounds = Rect(0, 0, (imageWidth * scale).toInt(), (imageHeight * scale).toInt())
                    val destBounds = Rect(landmark.position.x.toInt(), landmark.position.x.toInt(), (imageWidth * scale).toInt(), (imageHeight * scale).toInt())

                    mBitmap_blurred =  bitmapBlur(faceBitmap!!,scale.toFloat(),5)
                    myCanvas.drawBitmap(mBitmap_blurred!!, null, destBounds, null)

                }*/
            }
            var facey = face.position.y.toInt()
            var facex = face.position.x.toInt()
            var draw_facey = face.position.y
            if(face.position.y<=0){
                facey = facey * -1;
                Log.d("facey","face y is negative")
                draw_facey = draw_facey  + 15
            }

            if(face.position.x<=0){
                facex = facex * -1 ;
                Log.d("facex","face x is negative")

            }

            Log.d("facey", "${facey}")
            Log.d("facex", "${facex}")

            val faceBitMap = Bitmap.createBitmap(
                myBitmap,
                facex,
                facey,
                face.width.toInt(),
                face.height.toInt()
            )

            val viewWidth = myCanvas.width.toDouble()
            val viewHeight = myCanvas.height.toDouble()
            val imageWidth = faceBitMap!!.width.toDouble()
            val imageHeight = faceBitMap!!.height.toDouble()
            var scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight)
            scale = 1.0
            //val faceBitMap = Bitmap.createBitmap(face.width.toInt(), face.height.toInt(), Bitmap.Config.ARGB_4444)
            //val destBounds = Rect(0, 0, (imageWidth * scale).toInt(), (imageHeight * scale).toInt())
            //val destBounds = Rect(face.position.x.toInt(), facey, (imageWidth * scale).toInt(), (imageHeight * scale).toInt())
            //val destBounds = Rect(0, 0, (imageWidth * scale).toInt(), (imageHeight * scale).toInt())
            //val destBounds = Rect(face.position.x.toInt(), face.position.y.toInt()*-1, (face.width * scale).toInt(), (face.height * scale).toInt())

            mBitmap_blurred =  bitmapBlur(faceBitMap,1f,20)
            //myCanvas.drawBitmap(mBitmap_blurred!!, null, destBounds, rectPaint)
            myCanvas.drawBitmap(mBitmap_blurred!!, face.position.x, draw_facey, rectPaint)

        }
        imageView.setImageDrawable(BitmapDrawable(resources, tempBitmap))

        detector.release()

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //Log.d("accuracy", "in On accuracy Changed")

    }

    override fun onSensorChanged(event: SensorEvent) {
        //Log.d("sensortype", "${event.sensor.type.toString()}")
        if (event.sensor.type!= Sensor.TYPE_ACCELEROMETER)
            return

        //Log.d("sensror", "in On SensorChanged")
        val alpha: Float = 0.8f


        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0f
        if (deltaY < 2)
            deltaY = 0f
        if (deltaZ < 2)
            deltaZ = 0f

        if ((deltaZ > blurThreshold) || (deltaY > blurThreshold) || (deltaZ > blurThreshold))
        {

            Log.d("lastx", "$lastX")

            if(lastX!=0f){
                blurFace()

            }

        }

        lastX = event.values[0]
        lastY = event.values[1]
        lastZ = event.values[2]
    }

    override fun onPause()
    {
        super.onPause()
        msensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        msensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_GAME)
    }


    private fun bitmapBlur(sentBitmap: Bitmap, scale: Float, radius: Int): Bitmap? {
        var sentBitmap = sentBitmap

        val width = Math.round(sentBitmap.width * scale)
        val height = Math.round(sentBitmap.height * scale)
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false)

        val bitmap = sentBitmap.copy(sentBitmap.config, true)

        if (radius < 1) {
            return null
        }

        val w = bitmap.width
        val h = bitmap.height

        val pix = IntArray(w * h)
        Log.e("pix", w.toString() + " " + h + " " + pix.size)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }

        Log.e("pix", w.toString() + " " + h + " " + pix.size)
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)

        return bitmap

    }
}
