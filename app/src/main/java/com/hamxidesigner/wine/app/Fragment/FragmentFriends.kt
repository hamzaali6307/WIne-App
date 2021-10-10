@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")package com.hamxidesigner.wine.app.Fragmentimport android.annotation.SuppressLintimport android.app.Activity.RESULT_CANCELEDimport android.app.Activity.RESULT_OKimport android.content.Intentimport android.graphics.Colorimport android.os.Bundleimport android.util.Logimport android.view.*import android.widget.RelativeLayoutimport android.widget.TextViewimport android.widget.Toastimport androidx.fragment.app.Fragmentimport androidx.recyclerview.widget.DefaultItemAnimatorimport androidx.recyclerview.widget.LinearLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport com.google.android.gms.tasks.OnSuccessListenerimport com.google.android.material.snackbar.Snackbarimport com.google.firebase.auth.FirebaseAuthimport com.hamxidesigner.wine.app.Rimport com.google.firebase.auth.FirebaseUserimport com.google.firebase.firestore.FirebaseFirestoreimport com.google.firebase.firestore.Queryimport com.google.firebase.firestore.QuerySnapshotimport com.hamxidesigner.wine.app.Activities.SearchingActivityimport com.hamxidesigner.wine.app.Adapter.FriendsAdapterimport com.hamxidesigner.wine.app.Helper.RecyclerViewTouchListenerimport com.hamxidesigner.wine.app.Helper.RecyclerViewTouchListener.ClickListenerimport com.hamxidesigner.wine.app.Model.DataModelimport com.kaopiz.kprogresshud.KProgressHUDimport com.wafflecopter.multicontactpicker.LimitColumnimport com.wafflecopter.multicontactpicker.MultiContactPickerimport com.wafflecopter.multicontactpicker.RxContacts.PhoneNumberimport kotlinx.android.synthetic.main.activity_signup.*import java.util.regex.Patternimport com.google.firebase.firestore.CollectionReference as CollectionReference1@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNREACHABLE_CODE")open class FragmentFriends : Fragment() {    private lateinit var dataAdapter: FriendsAdapter    private lateinit var recyclerView: RecyclerView    private lateinit var arrayList: ArrayList<DataModel>    private lateinit var filter: ArrayList<DataModel>    private lateinit var mView: View    private lateinit var eror: TextView    lateinit var relitive: RelativeLayout    lateinit var dataModel: DataModel    public var CONTACT_PICKER_REQUEST = 100    lateinit var currentFirebaseUser: FirebaseUser    var checkFriendQuery: Query? = null    lateinit var firestore: FirebaseFirestore    lateinit var wineIdAray: ArrayList<String>    var checkFriendNumbQuery: Query? = null    var checkFriendWineQuery: Query? = null    var id_type_en: String = ""    var id_type_es: String = ""    lateinit var wideIdAray: ArrayList<String>    lateinit var kProgressHUD: KProgressHUD    lateinit var db: FirebaseFirestore    var docRefColWinTyp: CollectionReference1 = FirebaseFirestore.getInstance().collection("wine_type")    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {        mView = inflater.inflate(R.layout.fragment_main, container, false)        recyclerView = mView.findViewById(R.id.recyclerview)        eror = mView.findViewById(R.id.eror)        relitive = mView.findViewById(R.id.relitive)        arrayList = ArrayList<DataModel>()        wideIdAray = ArrayList<String>()        filter = ArrayList<DataModel>()        wineIdAray = ArrayList<String>()        dataModel = DataModel()        initViews()        initObect()        clickView()        return mView    }    @SuppressLint("RestrictedApi")    private fun initViews() {        firestore = FirebaseFirestore.getInstance()        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!        initProgressDialog()        setHasOptionsMenu(true)        checkFriends()    }    private fun checkFriends() {        checkFriendQuery = FirebaseFirestore.getInstance().collection("friends").whereEqualTo("user_id", currentFirebaseUser.uid)        checkFriendQuery!!.get().addOnCompleteListener { task ->            if (task.isSuccessful && task.getResult()!!.documents.isNotEmpty()) {                for (document in task.result!!) {                    val friend_id: String = document.getString("friend_id") as String                    getWineIdOffriendsLike(friend_id)                }            } else {                eror.text = "No friend  have this app yet"                eror.visibility = View.VISIBLE                kProgressHUD.dismiss()                var snackbar = Snackbar                        .make(relitive, "No friend of Your have this app yet", Snackbar.LENGTH_LONG)                snackbar.show()                Log.d("dataaError", "Error getting documents: ", task.exception)            }        }.addOnFailureListener { runnable: Exception? ->            kProgressHUD.dismiss()            eror.visibility = View.VISIBLE            var snackbar = Snackbar                    .make(relitive, "some thing went wrong", Snackbar.LENGTH_LONG)            snackbar.show()        }    }    private fun getWineIdOffriendsLike(friendId: String) {        kProgressHUD.dismiss()        checkFriendWineQuery = FirebaseFirestore.getInstance().collection("favourites").whereEqualTo("person_id", friendId)        checkFriendWineQuery!!.get().addOnCompleteListener { task ->            if (task.isSuccessful) {                eror.visibility = View.GONE                var size = task.result!!.documents.size                for (document in 0 until size) {                    var totel_fav_wine = size                    val wine_id = task.result!!.documents[document].get("wine_id") as String                    wineIdAray.add(wine_id)                    Log.d("friends_wine_id", "" + wine_id)                    dataModel = DataModel(friendId, wine_id, totel_fav_wine.toString())                    // prepareData(wine_id)                }                dataModel.wineIdCollection                arrayList.add(dataModel)                kProgressHUD.dismiss()                dataAdapter.notifyDataSetChanged()            } else {                kProgressHUD.dismiss()                var snackbar = Snackbar                        .make(relitive, "your friends dn't have any fav wine yet", Snackbar.LENGTH_LONG)                snackbar.show()                Log.d("dataaError", "Error getting documents: ", task.exception)            }        }.addOnFailureListener { runnable: Exception? ->            kProgressHUD.dismiss()            var snackbar = Snackbar                    .make(relitive, "some thing went wrong", Snackbar.LENGTH_LONG)            snackbar.show()        }    }    private fun clickView() {        recyclerView.addOnItemTouchListener(RecyclerViewTouchListener(context, recyclerView, object : ClickListener {            override fun onClick(view: View?, position: Int) {                val movie: DataModel = arrayList[position]                var intent = Intent(context, SearchingActivity::class.java)                intent.putExtra("data", movie.friendId)                startActivity(intent)            }            override fun onLongClick(view: View?, position: Int) {}        }))    }    private fun initObect() {        db = FirebaseFirestore.getInstance()        kProgressHUD.show()        dataAdapter = context?.let { FriendsAdapter(arrayList, it, arrayList) }!!        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)        recyclerView.layoutManager = mLayoutManager        recyclerView.itemAnimator = DefaultItemAnimator()        recyclerView.adapter = dataAdapter    }    private fun prepareData(wine_id: String) {        var docRefCollecMain: Query = FirebaseFirestore.getInstance().collection("wines").whereEqualTo("wine_id", wine_id)        docRefCollecMain.get().addOnSuccessListener { documentSnapshot ->            if (!documentSnapshot.isEmpty) {                var test = documentSnapshot.documents                Log.d("wineess", test.size.toString())                for (documntName in test.indices) {                    var country_origin = test[documntName].get("country_origin") as String                    var ean = test[documntName].get("ean") as String                    var pdo_id = test[documntName].get("pdo_id") as String                    var pic = test[documntName].get("pic") as String                    var price_id = test[documntName].get("price_id") as String                    var wine_id = test[documntName].get("wine_id") as String                    var wine_name = test[documntName].get("wine_name") as String                    var wine_type = test[documntName].get("wine_type") as String                    var year = test[documntName].get("year") as String                    dataModel = DataModel(                            test.toString(),                            country_origin,                            ean,                            pdo_id,                            pic,                            price_id,                            wine_id,                            wine_name,                            wine_type,                            year,                            "",                            "",                            "")                    arrayList.add(dataModel)                    kProgressHUD.dismiss()                    dataAdapter.notifyDataSetChanged()                }            } else {                kProgressHUD.dismiss()                var snackbar = Snackbar                        .make(relitive, "No data found", Snackbar.LENGTH_LONG)                snackbar.show()            }        }    }    private fun getWineType(test: String, wineId: String, country_origin: String                            , ean: String, pdo_id: String, pic: String, price_id: String                            , wine_name: String, wine_type: String, year: String) {        docRefColWinTyp.whereEqualTo("id_wine_type", wineId).get()                .addOnSuccessListener { documents ->                    if (!documents.isEmpty) {                        // for (document in documents.documents) {                        Log.d("size", documents.documents.size.toString())                        id_type_en = documents.documents[0].get("id_type_en").toString()                        id_type_es = documents.documents[0].get("id_type_es").toString()                    }                }.addOnFailureListener {                    Log.d("not found", "dsdfer")                }        dataModel = DataModel(                test.toString(),                country_origin,                ean,                pdo_id,                pic,                price_id,                wineId,                wine_name,                wine_type,                year,                id_type_en,                id_type_es,                "")        arrayList.add(dataModel)        kProgressHUD.dismiss()        dataAdapter.notifyDataSetChanged()    }    fun newInstance(): FragmentFriends {        return FragmentFriends()    }    fun initProgressDialog() {        kProgressHUD = KProgressHUD.create(activity)                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)                .setLabel("Please wait")                .setDetailsLabel("Fetching data")                .setCancellable(false)                .setAnimationSpeed(4)                .setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))                .setDimAmount(0.5f)    }    override fun onDestroy() {        super.onDestroy()        kProgressHUD.dismiss()    }    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {        inflater.inflate(R.menu.search, menu)        super.onCreateOptionsMenu(menu, inflater)    }    override fun onOptionsItemSelected(item: MenuItem): Boolean {        return when (item.itemId) {            R.id.start_excercise -> {                MultiContactPicker.Builder(this) //Activity/fragment context                        .hideScrollbar(false) //Optional - default: false                        .showTrack(true) //Optional - default: true                        .searchIconColor(Color.WHITE) //Option - default: White                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE                        .bubbleTextColor(Color.WHITE) //Optional - default: White                        .setTitleText("Select Contacts") //Optional - default: Select Contacts                        .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)                        .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)                        .setActivityAnimations(R.anim.slide_in, R.anim.slide_out,                                R.anim.slide_in,                                R.anim.slide_out) //Optional - default: No animation overrides                        .showPickerForResult(CONTACT_PICKER_REQUEST)                true            }            else -> super.onOptionsItemSelected(item)        }        return super.onOptionsItemSelected(item)    }    private fun checkContactNoInlist(number: String, number1: String) {        checkFriendNumbQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("contact_no", number1)        checkFriendNumbQuery!!.get().addOnCompleteListener { task ->            if (task.isSuccessful && task.result!!.documents.isNotEmpty()) {                for (document in task.result!!) {                    val friend_id: String = document.getString("user_id") as String                    checkFriendAlreadyExists(friend_id)                }            } else {                ////                checkFriendNumbQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("contact_noc", number1)                checkFriendNumbQuery!!.get().addOnCompleteListener { task ->                    if (task.isSuccessful && task.result!!.documents.isNotEmpty()) {                        for (document in task.result!!) {                            val friend_id: String = document.getString("user_id") as String                            checkFriendAlreadyExists(friend_id)                        }                    } else {                        kProgressHUD.dismiss()                        var snackbar = Snackbar                                .make(relitive, "Your friend don't have this app yet", Snackbar.LENGTH_LONG)                        snackbar.show()                    }                }.addOnFailureListener { runnable: Exception? ->                    kProgressHUD.dismiss()                    var snackbar = Snackbar                            .make(relitive, "some thing went wrong", Snackbar.LENGTH_LONG)                    snackbar.show()                }                /////            }        }.addOnFailureListener { runnable: Exception? ->            kProgressHUD.dismiss()            Toast.makeText(activity, "some thing went wrong", Toast.LENGTH_SHORT).show()        }    }    private fun checkFriendAlreadyExists(friendId: String) {        checkFriendNumbQuery = FirebaseFirestore.getInstance().collection("friends").whereEqualTo("user_id", currentFirebaseUser.uid)        checkFriendNumbQuery!!.whereEqualTo("friend_id", friendId)//        checkFriendNumbQuery!!.get().addOnSuccessListener { querySnapshot: QuerySnapshot? ->//            for (documnt in querySnapshot!!.documents.iterator()  ){//                val friend_ids: String = documnt.get("friend_id") as String//                if(friendId==friend_ids){//                    var snackbar = Snackbar//                            .make(relitive, "This friend is already in your friend list", Snackbar.LENGTH_LONG)//                    snackbar.show()//                }else {////                    saveFriendIdInList(friendId, currentFirebaseUser.uid)//                }////            }//        }        checkFriendNumbQuery!!.get().addOnCompleteListener { task ->            if (task.isSuccessful) {                if (task.result!!.documents.isNotEmpty()) {                    for (document in task.result!!) {                        val friend_ids: String = document.getString("friend_id") as String                        if (friendId == friend_ids) {                            kProgressHUD.dismiss()                            var snackbar = Snackbar                                    .make(relitive, "No friend of Your have this app yet", Snackbar.LENGTH_LONG)                            snackbar.show()                        } else {                            saveFriendIdInList(friendId, currentFirebaseUser.uid)                        }                    }                } else {                    saveFriendIdInList(friendId, currentFirebaseUser.uid)                    kProgressHUD.dismiss()                }            }       }    }    private fun saveFriendIdInList(friendId: String, uid: String) {        var data = HashMap<String, String>()        data.put("user_id", uid)        data.put("friend_id", friendId)        firestore.collection("friends").add(data).addOnSuccessListener { documentReference ->            Toast.makeText(activity, "friend added successfully", Toast.LENGTH_LONG).show()            arrayList.clear()            dataAdapter.notifyDataSetChanged()            checkFriends()        }.addOnFailureListener() {            Log.d("Error", "Some thing went wrong")        }    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)        if (requestCode == 100) {            if (resultCode == RESULT_OK) {                var results = MultiContactPicker.obtainResult(data)                results[0].phoneNumbers                for (test in results.iterator()) {                    Log.d("Contactno", results[0].phoneNumbers[0].number)                    //  var contactNo =  results[0].phoneNumbers[0].number.substring(1)                    var contactNo: String = results[0].phoneNumbers[0].number                    Log.d("Contactno", contactNo)                    var pattern = Pattern.compile("[^0-9]")                    var matcher = pattern.matcher(contactNo)                    var number = matcher.replaceAll("")                    var valueee = number[0]                    if (valueee.equals(0)) {                        val newValue = contactNo.substring(1)                        checkContactNoInlist(contactNo, newValue)                    } else {                        checkContactNoInlist(contactNo, number)                    }                }            } else if (resultCode == RESULT_CANCELED) {                System.out.println("User closed the picker without selecting items.")            }        }    }}