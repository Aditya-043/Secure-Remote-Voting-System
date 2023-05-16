package com.sunayanpradhan.onlinevoting.Activities


import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sunayanpradhan.onlinevoting.Models.VoterInformation
import com.sunayanpradhan.onlinevoting.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var aadhaarNo: EditText
    lateinit var voterNo: EditText
    lateinit var phoneNo: EditText
    lateinit var verifyButton: Button
    lateinit var otpNo: EditText
    lateinit var faceCaptureButton:CircleImageView
    lateinit var getStarted: Button
    lateinit var progressBar: ProgressBar

    lateinit var auth: FirebaseAuth

    private var storedVerificationId = ""

    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken

    private var isExist=false

    private val CAMERA_REQUEST=100

    private var isFaceVerified= false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aadhaarNo = findViewById(R.id.aadhaar_no)
        voterNo = findViewById(R.id.voter_no)
        phoneNo = findViewById(R.id.phone_no)
        verifyButton = findViewById(R.id.verify_button)
        otpNo = findViewById(R.id.otp_no)
        faceCaptureButton= findViewById(R.id.face_capture_button)
        getStarted = findViewById(R.id.get_started)
        progressBar= findViewById(R.id.progress_bar)

        auth = FirebaseAuth.getInstance()

        FirebaseApp.initializeApp(this)

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {


                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {



                }
            }).check()




        verifyButton.setOnClickListener {

            if (phoneNo.text.startsWith("+91")){
                if (phoneNo.length()==13) {

                    startPhoneNumberVerification(phoneNo.text.toString())


                }
                else{
                    Toast.makeText(this, "Enter phone number correctly", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                if (phoneNo.length()==10) {
                    startPhoneNumberVerification("+91${phoneNo.text}")



                }
                else{
                    Toast.makeText(this, "Enter phone number correctly", Toast.LENGTH_SHORT).show()
                }
            }


        }

        FirebaseDatabase.getInstance().reference.child("Voters").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {

                    var user = dataSnapshot.getValue(VoterInformation::class.java)

                    user?.userId = dataSnapshot.key.toString()

                    if (aadhaarNo.text.toString()==user?.aadhaarId||
                        voterNo.text.toString()==user?.voterId){

                        isExist=true

                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        faceCaptureButton.setOnClickListener {

            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){


                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)


            }
            else{

                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.CAMERA
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                            Toast.makeText(this@MainActivity, "CAMERA PERMISSION GRANTED", Toast.LENGTH_SHORT).show()

                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, CAMERA_REQUEST)

                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest?>?,
                            token: PermissionToken?
                        ) {

                            Toast.makeText(this@MainActivity, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show()


                        }
                    }).check()



                Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show()



            }



        }


        getStarted.setOnClickListener {

            if (aadhaarNo.text.toString().trim().isEmpty() ||
                voterNo.text.toString().trim().isEmpty() ||
                phoneNo.text.toString().trim().isEmpty() ||
                otpNo.text.toString().trim().isEmpty())
            {

                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()

            }

            else if (aadhaarNo.length()!=12){

                Toast.makeText(this, "Enter your Aadhaar no correctly", Toast.LENGTH_SHORT).show()

            }

            else if (!isFaceVerified){

                Toast.makeText(this, "Verify Your Face Id First", Toast.LENGTH_SHORT).show()


            }

            else {

                verifyPhoneNumberWithCode(storedVerificationId, otpNo.text.toString())


            }



        }

//        checkUser()


    }

    private fun checkUser(){

        if (auth.currentUser!=null){

            val intent=Intent(this,VoteCategoryActivity::class.java)

            overridePendingTransition(0,0)

            startActivity(intent)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === CAMERA_REQUEST && resultCode === Activity.RESULT_OK) {
            val bitmap: Bitmap = data!!.extras!!["data"] as Bitmap

            val image = InputImage.fromBitmap(bitmap, 0)

            val detector = FaceDetection.getClient()

            val result = detector.process(image)
                .addOnSuccessListener { faces ->

                    if (faces.isEmpty()){

                        Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show()

                    }

                    else if (faces.count()>1){

                        Toast.makeText(this, "Multiple face detected", Toast.LENGTH_SHORT).show()

                    }

                    else{

                        faceCaptureButton.setImageBitmap(image.bitmapInternal)

                        Toast.makeText(this, "Face Verified", Toast.LENGTH_SHORT).show()

                        isFaceVerified= true

                    }

                }
                .addOnFailureListener { e ->

                    Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show()

                }

        }

    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationId, code)


        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        progressBar.visibility= View.VISIBLE

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()

                    progressBar.visibility= View.GONE

                    val user = task.result?.user

                    if (task.result?.additionalUserInfo?.isNewUser!!){


                        sendDataToDatabase()

                    }

                    else{


                        FirebaseDatabase.getInstance().reference.child("Voters").child(FirebaseAuth.getInstance().uid.toString()).addValueEventListener(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                val data:VoterInformation?=snapshot.getValue(VoterInformation::class.java)

                                data?.userId=snapshot.key.toString()

                                if (aadhaarNo.text.toString().trim()==data?.aadhaarId &&
                                    voterNo.text.toString().trim()==data.voterId &&
                                    phoneNo.text.toString().trim()==data.phoneNo){


                                    val intent=Intent(this@MainActivity,VoteCategoryActivity::class.java)

                                    startActivity(intent)


                                }

                                else{

                                    Toast.makeText(this@MainActivity, "Enter all data correctly", Toast.LENGTH_SHORT).show()

                                    FirebaseAuth.getInstance().signOut()


                                }


                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                            }

                        })


                    }



                } else {

                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()

                    progressBar.visibility= View.GONE

                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }






    }

    private fun disableEditText(editText: EditText) {
//        editText.isFocusable = false
        editText.isEnabled = false

    }

    private fun enableEditText(editText: EditText) {
//        editText.isFocusable = true
        editText.isEnabled = true
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {

        verifyButton.isEnabled=false

        disableEditText(phoneNo)

        verifyButton.setBackgroundColor(Color.GRAY)

        Toast.makeText(this, "OTP Send", Toast.LENGTH_SHORT).show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


        Handler().postDelayed({
            verifyButton.isEnabled=true

            verifyButton.setBackgroundColor(Color.BLACK)

            enableEditText(phoneNo)

            }, 60000)


    }


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }


    private fun sendDataToDatabase(){

        val firebaseUser: FirebaseUser? = auth.currentUser

        val user = VoterInformation(aadhaarNo.text.toString(),
            voterNo.text.toString(),
            phoneNo.text.toString(),
            firebaseUser?.uid.toString())

        if (firebaseUser != null) {
            FirebaseDatabase.getInstance().reference.child("Voters").child(firebaseUser.uid)
                .setValue(user).addOnSuccessListener {

                    val intent=Intent(this,VoteCategoryActivity::class.java)

                    startActivity(intent)

                }

        }


    }

    override fun onStart() {
        super.onStart()

        checkUser()

    }



}