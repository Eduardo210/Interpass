package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_tags.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.entity.MovesModel
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.domain.model.Tag
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.adapter.MovesRecyclerAdapter
import mx.com.nut.basecleanarquitecture.presentation.adapter.TagPagerAdapter
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import org.ksoap2.serialization.SoapObject
import com.journeyapps.barcodescanner.BarcodeEncoder
import mx.com.nut.basecleanarquitecture.presentation.fragment.NewTagFragment
import mx.com.nut.basecleanarquitecture.presentation.fragment.TagViewFragment

import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.side_menu.*
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.commons.sendToView
import mx.com.nut.basecleanarquitecture.data.entity.MovesToBillingModel
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseSession
import mx.com.nut.basecleanarquitecture.domain.model.ItemMenu
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogLottie
import mx.com.nut.basecleanarquitecture.presentation.adapter.MenuAdapter
import mx.com.nut.basecleanarquitecture.presentation.base.Session

class TagsActivity: BaseActivity(), MovesRecyclerAdapter.OnItemClickListener, CustomDialog.OnClickSetListener, TagViewFragment.ClickEdit, NewTagFragment.ClickNew {
    private lateinit var toggle: ActionBarDrawerToggle
    private var arrayTags: ArrayList<Tag> = ArrayList()
    var movesArray: List<MovesModel> = ArrayList()
    var movesArrayDemo: ArrayList<MovesModel> = ArrayList()
    var movesFromTag: ArrayList<MovesModel> = ArrayList()
    var movesBillingArray: ArrayList<MovesToBillingModel> = ArrayList()

    lateinit var adapter: TagPagerAdapter
    lateinit var rvadapter: MovesRecyclerAdapter
    var userID = ""
    var tagNumberS = ""
    var cardPosition = 0
    var intentIndex = 0

    var handler: Handler = Handler()
    var handlerTags: Handler = Handler()
    var runnable: Runnable? = null
    var runnableTags: Runnable? = null

    private lateinit var movesRunnable: Runnable
    var movesHandler: Handler = Handler()

    var scroll = false
    var goToView = false
    var validateAccount = true
    var positionScroll = 0

    var responseCode = ""
    var mensajeLogin = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_tags
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        scroll = intent.extras.getBoolean("canScroll")
        validateAccount = intent.extras.getBoolean("validateAccount")
        if(intent.extras.getString("mensaje")!=null){
            mensajeLogin = intent.extras.getString("mensaje")
        }
        setToolbar()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        refresh()
        btnTopUpBalance.setOnClickListener {
            btnTopUpBalance.isEnabled = false
            val bundle = Bundle()
            bundle.putString("tagnumber", tagNumberS)
            bundle.putString("mensaje", mensajeLogin)
            goToView = true
            Session.updatePayments = true
            Commons.sendToViewWithBundle(this, PurchasesActivity::class.java, bundle)
        }
        btnBilling.setOnClickListener {
            //if (arrayTags[cardPosition].moves.size > 0) {
            if (arrayTags[positionScroll].moves.size > 0) {
                if (movesBillingArray.size >= 0) {
                    //btnBilling.isEnabled = false
                    val bundle = Bundle()
                    bundle.putString("tagnumber", tagNumberS)
                    //var items = arrayTags[cardPosition].moves
                    var items = arrayTags[positionScroll].moves
                    bundle.putParcelableArrayList("moves", items)
                    goToView = true
                    Session.updateBillingData = true
                    Commons.sendToViewWithBundle(this, BillingMovesActivity::class.java, bundle)
                }
            }
        }
        btnQRCode.setOnClickListener {
            btnQRCode.isEnabled = false
            val bundle = Bundle()
            bundle.putString("tagnumber", tagNumberS)
            goToView = true
            Commons.sendToViewWithBundle(this, QRActivity::class.java, bundle)
        }
        btnQuestion.setOnClickListener {
            val dialog =
                CustomDialogLottie(this, getString(R.string.titleUpdate), "0")
            dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.show()
        }
        if (scroll) {
            positionScroll = intent.extras.getInt("positionScroll")
        }
        getTags()
        if (validateAccount) {
            getSession()
        }

        setSideMenu()

    }

    override fun onResume() {
        super.onResume()
        if (Session.updateTagMoves) {
            movesArray = ArrayList()
            movesArrayDemo = ArrayList()
            getMoves(arrayTags[positionScroll].tagNumber)
            Session.updateTagMoves = false
        }
        btnTopUpBalance.isEnabled = true
        btnQRCode.isEnabled = true
        btnBilling.isEnabled = true
        Commons.deletePreference(this, "tagNumber")
        Commons.deletePreference(this, "digit")
        Commons.deletePreference(this, "plate")
        Commons.deletePreference(this, "nickName")
        Commons.deletePreference(this, "checked")
        Commons.deletePreference(this, "vehiclePosition")
        Commons.deletePreference(this, "prefixPosition")
        //runnableTags = Runnable {
          //  getTags()
        //}
        if (Session.launchHandler) {
            handlerTags.postDelayed(runnableTags, 300000)
        }
        Session.launchHandler = true
        if(responseCode =="128" || mensajeLogin == "OK"){
            btnQRCode.visibility = View.GONE
            btnBilling.visibility = View.GONE
            btnTopUpBalance.visibility = View.GONE
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    private fun addCarousel() {
        carousel.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                tagNumberS = arrayTags[position].tagNumber
                movesArray = ArrayList()
                movesArrayDemo = ArrayList()
                rvMoves.visibility = View.INVISIBLE
                noMovesView.visibility = View.INVISIBLE
                btnBilling.visibility = View.INVISIBLE
                if (position + 1 == arrayTags.size) {
                    btnTopUpBalance.visibility = View.INVISIBLE
                    btnQRCode.visibility = View.INVISIBLE
                    btnQuestion.visibility = View.GONE
                    //swipe_refresh_layout.isEnabled = false
                    handler.removeCallbacks(runnable)
                } else {
                    positionScroll = position
                    btnQuestion.visibility = View.VISIBLE
                    btnQRCode.visibility = View.VISIBLE
                    addMoves(position)
                    //swipe_refresh_layout.isEnabled = true
                    arrayTags[position].moves.forEach { move ->
                        val moveID: String? = move.tipoMovimientoID
                        when (moveID) {
                            "101", "102", "103", "104" -> {
                                btnBilling.visibility = View.VISIBLE
                            }
                        }
                    }
                    if (arrayTags[position].tagType == "POS-PAGO") {
                        btnTopUpBalance.visibility = View.INVISIBLE
                        btnQRCode.visibility = View.INVISIBLE
                        if (arrayTags[position].statusTag == "Activo") {
                            btnQRCode.visibility = View.VISIBLE
                        }
                    } else {
                        if (arrayTags[position].statusTag == "Activo") {
                            btnTopUpBalance.visibility = View.VISIBLE
                            btnQRCode.visibility = View.VISIBLE
                        } else if (arrayTags[position].statusTag == "Saldo Bajo") {
                            btnTopUpBalance.visibility = View.VISIBLE
                            btnQRCode.visibility = View.INVISIBLE
                        } else {
                            btnTopUpBalance.visibility = View.INVISIBLE
                            btnQRCode.visibility = View.INVISIBLE
                        }
                    }
                    if(responseCode =="128" || mensajeLogin == "OK"){
                        btnQRCode.visibility = View.GONE
                        btnBilling.visibility = View.GONE
                        btnTopUpBalance.visibility = View.GONE
                    }
                    //User Validation

                    //refreshing(position)
                }
                //refreshing()

            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun addMoves(pos: Int) {
        handler.removeCallbacks(runnable)
        movesArray = ArrayList()
        movesArrayDemo = ArrayList()
        rvMoves.visibility = View.INVISIBLE
        noMovesView.visibility = View.INVISIBLE
        handler.removeCallbacks(runnable)
        runnable = Runnable {
            if (arrayTags[pos].moves.size == 0) {
                cardPosition = pos
                movesFromTag = ArrayList()
                if (!arrayTags[pos].getCurrentMoves) {
                    getMoves(arrayTags[pos].tagNumber)
                } else {
                    rvMoves.visibility = View.INVISIBLE
                    noMovesView.visibility = View.VISIBLE
                }
            } else {
                noMovesView.visibility = View.INVISIBLE
                rvMoves.visibility = View.VISIBLE
                movesArray = arrayTags[pos].moves
                rvMoves.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvadapter = MovesRecyclerAdapter( this, this)
                rvMoves.adapter = rvadapter
                rvadapter.setElement(movesArray)
            }
        }
        handler.postDelayed(runnable, 1500)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        handlerTags.removeCallbacks(runnableTags)
    }

    private fun getSession() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("UsuId" to userID)
        RestService.baseRequest4(this,
            Constans.VERIFY_ACCOUNT, params, { response ->
                runOnUiThread {
                    val response = response as ResponseSession
                    responseCode = response.code!!
                    if (response.code == "0") {
                        val dialog =
                            CustomDialog(this, this, getString(R.string.titleError), "Tu contraseña ha cambiado, debe ingresar nuevamente con su nueva contraseña", success = false, action = false)
                        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog.setCancelable(false)
                        dialog.show()
                        return@runOnUiThread
                    } else if (response.code != "128" || response.code != "129") {
                    } else{
                        val dialog =
                            CustomDialog(this, this, getString(R.string.titleError), response.code!!, success = false, action = false)
                        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog.setCancelable(false)
                        dialog.show()
                        return@runOnUiThread
                    }
                    if(responseCode =="128" || mensajeLogin == "OK"){
                        btnQRCode.visibility = View.GONE
                        btnBilling.visibility = View.GONE
                        btnTopUpBalance.visibility = View.GONE
                    }
                }
            }, { error ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                    val barCodeEncoder = BarcodeEncoder()
                    val bitmap= barCodeEncoder.encodeBitmap("123456", BarcodeFormat.CODABAR, 250, 40)
                    arrayTags.add(
                        Tag(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            bitmap,
                            false,
                            movesFromTag,
                            ""

                        )
                    )
                    adapter = TagPagerAdapter(supportFragmentManager, arrayTags, this, this)
                    carousel.adapter = adapter
                    carousel.currentItem = 0
                    carousel.offscreenPageLimit = 1
                    carousel.pageMargin = resources.getDimensionPixelSize(R.dimen.dpn90)

                    swipe_refresh_layout.isEnabled = false
                }

            }, { exception ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()

                }
            })
    }

   fun refresh(){
        swipe_refresh_layout.setOnRefreshListener {
            // Initialize a new Runnable
            movesRunnable = Runnable {
                // Hide swipe to refresh icon animation
                movesArray = ArrayList()
                movesArrayDemo = ArrayList()
                addMoves(positionScroll)
                //getMoves(arrayTags[positionScroll].tagNumber)
                swipe_refresh_layout.isRefreshing = false
            }

            // Execute the task after specified time
            movesHandler.postDelayed(movesRunnable, 1000)
        }
    }

    fun refreshing(po : Int){
            // Initialize a new Runnable
            movesRunnable = Runnable {
                // Hide swipe to refresh icon animation
                movesArray = ArrayList()
                movesArrayDemo = ArrayList()
                getMoves(arrayTags[po].tagNumber)
                swipe_refresh_layout.isRefreshing = false
            }

            // Execute the task after specified time
            movesHandler.postDelayed(movesRunnable, 1000)

    }



    private fun getTags() {
        arrayTags = ArrayList()
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID)
        RestService.baseRequest(this,
            RequestEnum.GETTAG, Constans.METHOD_GET_TAGS, params, { response ->
                runOnUiThread {
                    val responseGetTag = response as ResponseList
                    val tags = responseGetTag.list as SoapObject
                    var count = 0

                    while (count < tags.propertyCount) {
                        val tag = tags.getProperty(count) as SoapObject
                        val tagNumber = tag.getPrimitivePropertyAsString("Tag_Id")
                        val barCodeEncoder = BarcodeEncoder()
                        val bitmap= barCodeEncoder.encodeBitmap(tagNumber, BarcodeFormat.CODE_128, getResources().getDimensionPixelSize(R.dimen.dp350), getResources().getDimensionPixelSize(R.dimen.dp40))
                        arrayTags.add(
                            Tag(
                                tag.getPrimitivePropertyAsString("EstatusTag"),
                                tag.getPrimitivePropertyAsString("FechaAlta"),
                                tag.getPrimitivePropertyAsString("Num_Economico"),
                                tag.getPrimitivePropertyAsString("Saldo"),
                                tagNumber,
                                tag.getPrimitivePropertyAsString("TipoTag"),
                                tag.getPrimitivePropertyAsString("Vehiculo_Clase"),
                                tag.getPrimitivePropertyAsString("Vehiculo_Placas"),
                                tag.getPrimitivePropertyAsString("idTipo_Tag"),
                                bitmap,
                                false,
                                movesFromTag,
                                tag.getPrimitivePropertyAsString("EsPredeterminado")
                            )
                        )
                        count += 1
                    }

                    arrayTags.sortWith(compareByDescending { it.esPrederterminado })
                    val barCodeEncoder = BarcodeEncoder()
                    val bitmap= barCodeEncoder.encodeBitmap("123456", BarcodeFormat.CODABAR, 250, 40)
                    arrayTags.add(
                        Tag(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            bitmap,
                            false,
                            movesFromTag,
                            ""

                        )
                    )
                    adapter = TagPagerAdapter(supportFragmentManager, arrayTags, this, this)
                    carousel.adapter = adapter
                    carousel.currentItem = 0
                    carousel.offscreenPageLimit = 1
                    carousel.pageMargin = resources.getDimensionPixelSize(R.dimen.dpn90)
                    if (arrayTags[0].tagType == "POS-PAGO") {
                        btnTopUpBalance.visibility = View.INVISIBLE
                    } else {
                        btnTopUpBalance.visibility = View.VISIBLE
                    }
                    tagNumberS = arrayTags[0].tagNumber
                    addCarousel()
                    if (scroll || goToView) {
                        val pos = positionScroll
                        carousel.currentItem = pos
                        addMoves(pos)
                    } else {
                        addMoves(0)
                    }

                    if (arrayTags[0].statusTag == "Saldo Bajo") {
                        btnQRCode.visibility = View.INVISIBLE
                    }

                    if (arrayTags[0].statusTag != "Activo") {
                        btnQRCode.visibility = View.INVISIBLE
                    }
                    if(responseCode =="128" || mensajeLogin == "OK"){
                        btnQRCode.visibility = View.GONE
                        btnBilling.visibility = View.GONE
                        btnTopUpBalance.visibility = View.GONE
                    }


                }
            }, { error ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                    val barCodeEncoder = BarcodeEncoder()
                    val bitmap= barCodeEncoder.encodeBitmap("123456", BarcodeFormat.CODABAR, 250, 40)
                    arrayTags.add(
                        Tag(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            bitmap,
                            false,
                            movesFromTag,
                            ""

                        )
                    )
                    adapter = TagPagerAdapter(supportFragmentManager, arrayTags, this, this)
                    carousel.adapter = adapter
                    carousel.currentItem = 0
                    carousel.offscreenPageLimit = 1
                    carousel.pageMargin = resources.getDimensionPixelSize(R.dimen.dpn90)
                }
            }, { exception ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()

                }
            })
        handlerTags.postDelayed(runnableTags, 300000)
    }

    private fun getMoves(tag: String) {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("usuarioId" to userID, "fechaInicio" to  Utils.getDate("dd/MM/yyyy"), "dias" to "90", "tag" to tag)
        RestService.baseRequest(this,
            RequestEnum.GETMOVES, Constans.METHOD_GET_MOVES, params, { response ->
                stopLoader()
                runOnUiThread {
                    val responseGetMoves = response as ResponseList
                    val moves = responseGetMoves.list as SoapObject?
                    var count = 0
                    if (moves?.propertyCount == null) {
                        arrayTags[cardPosition].getCurrentMoves = true
                        noMovesView.visibility = View.VISIBLE
                        return@runOnUiThread
                    }
                    while (count < moves?.propertyCount!!) {
                        val move = moves?.getProperty(count) as SoapObject

                        val fullDate = move.getPrimitivePropertyAsString("Trn_FechaHora")
                        val date = fullDate.substringBefore(" ")
                        val hour = fullDate.substringAfter(" ")
                        var description = ""
                        move.getPrimitivePropertyAsString("Carril_Sentido")?.let { description = (it) }
                        if (description == "") {
                            description = ""
                        }
                        var carril = ""
                        move.getPrimitivePropertyAsString("Carril_Nombre")?.let { carril = (it) }
                        if (carril == "") {
                            carril = ""
                        }
                        movesArrayDemo.add(
                            MovesModel(
                                count,
                                date,
                                hour,
                                description,
                                move.getPrimitivePropertyAsString("Trn_ImporteTotal"),
                                move.getPrimitivePropertyAsString("TipoMovimiento"),
                                move.getPrimitivePropertyAsString("Saldo_Trans"),
                                carril,
                                move.getPrimitivePropertyAsString("Clase_Descripcion"),
                                move.getPrimitivePropertyAsString("Tag_Id"),
                                move.getPrimitivePropertyAsString("Plaza_Nombre"),
                                move.getPrimitivePropertyAsString("EstatusTag"),
                                move.getPrimitivePropertyAsString("TipoMovimientoId"),
                                move.getPrimitivePropertyAsString("Trn_Id")
                            )
                        )
                        count += 1
                    }
                    if (arrayTags[positionScroll].balance != movesArrayDemo[0].balance || (arrayTags[positionScroll].statusTag != movesArrayDemo[0].status && movesArrayDemo[0].status != "")) {
                        arrayTags[positionScroll].balance = movesArrayDemo[0].balance!!
                        // arrayTags[positionScroll].statusTag = movesArrayDemo[0].status!!
                        adapter.notifyDataSetChanged()
                    }
                    arrayTags[cardPosition].getCurrentMoves = true
                    if (movesArrayDemo.size > 0) {
                        arrayTags[cardPosition].moves = movesArrayDemo
                        arrayTags[cardPosition].moves.forEach { move ->
                            val moveID: String? = move.tipoMovimientoID
                            when (moveID) {
                                "101", "102", "103", "104" -> {
                                    btnBilling.visibility = View.VISIBLE
                                }
                            }
                        }
                        noMovesView.visibility = View.INVISIBLE
                        rvMoves.visibility = View.VISIBLE
                        movesArray = movesArrayDemo
                        rvMoves.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        rvadapter = MovesRecyclerAdapter( this, this)
                        rvMoves.adapter = rvadapter
                        rvadapter.setElement(movesArray)
                    } else {
                        rvMoves.visibility = View.INVISIBLE
                        noMovesView.visibility = View.VISIBLE
                    }

                }
            }, { error ->
                stopLoader()
                runOnUiThread {
                    rvMoves.visibility = View.INVISIBLE
                    noMovesView.visibility = View.VISIBLE
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }
            }, { exception ->
                stopLoader()
                runOnUiThread {
                    rvMoves.visibility = View.INVISIBLE
                    noMovesView.visibility = View.VISIBLE
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }
            })
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == "logOut") {
            logout()
        }

    }

    override fun onClickEditPressed(tagID: String) {
        val bundle = Bundle()
        arrayTags.forEach { tag ->
            if (tag.tagNumber == tagID) {
                bundle.putString("tagID", tag.tagNumber)
                bundle.putString("plate", tag.vehiclePlate)
                bundle.putString("nickName", tag.economicNum)
                bundle.putString("vehicleType", tag.vehicleClass)
                bundle.putString("status", tag.statusTag)
                bundle.putString("esPredeterminado", tag.esPrederterminado)
                bundle.putString("mensaje", mensajeLogin)
            }
        }
        bundle.putInt("positionScroll", positionScroll)
        goToView = true
        intentIndex = 1
        Commons.sendToViewWithBundle(this, DetailTagActivity::class.java, bundle)

    }

    override fun onClickNewPressed(tagID: String) {
        val bundle = Bundle()
        bundle.putString("sisUsu_Id", userID)
        bundle.putBoolean("newTag", true)
        bundle.putString("mensaje", mensajeLogin)
        goToView = true
        intentIndex = 1
        Commons.sendToViewWithBundle(this, RegisterTagActivity::class.java, bundle)
    }

    private fun setToolbar() {
        setSupportActionBar(navbar)
        toggle = ActionBarDrawerToggle(this, drawer_layout, navbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.setHomeAsUpIndicator(R.drawable.menu)
        drawer_layout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setSideMenu() {
        val layoutParams = imgLogo.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = getStatusBarHeight() + resources.getDimensionPixelSize(R.dimen.dp6)
        imgLogo.layoutParams = layoutParams
        val titles : Any
        val icons : Any
        if(mensajeLogin == "130"){
            titles = resources.obtainTypedArray(R.array.menu_titles)
            icons = resources.obtainTypedArray(R.array.menu_icons)
        }
        else{
            titles = resources.obtainTypedArray(R.array.menu_titles_status128)
            icons = resources.obtainTypedArray(R.array.menu_icons_status128)
        }

        val menuItems = ArrayList<ItemMenu>()

        for (position in 0 until titles.length()) {
            menuItems.add(
                ItemMenu(
                    title = titles.getResourceId(position, -1),
                    icon = icons.getResourceId(position, -1)
                )
            )
        }

        titles.recycle()
        icons.recycle()
        if(mensajeLogin== "130") {
            val adapter = MenuAdapter(menuItems) { pos ->
                when(pos) {
                    0 -> {
                        sendToView(MyAccountActivity::class.java)
                        intentIndex = 1
                        Session.updateProfile = true
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    1 -> {
                        sendToView(PaymentsActivity::class.java)
                        intentIndex = 1
                        Session.updatePayments = true
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    2 -> {
                        sendToView(BillingDataActivity::class.java)
                        intentIndex = 1
                        Session.updateBillingData = true
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    3 -> {
                        sendToView(NoticesActivity::class.java)
                        intentIndex = 1
                        Session.updateBillingData = true
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    4 -> {
                        sendToView(AboutActivity::class.java)
                        intentIndex = 1
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    5 -> logout()
                }
            }

            items.layoutManager = LinearLayoutManager(this)
            items.adapter = adapter

            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else
        {
            val adapter = MenuAdapter(menuItems) { pos ->
                when(pos) {
                    0 -> {
                        sendToView(MyAccountActivity::class.java)
                        intentIndex = 1
                        Session.updateProfile = true
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    1 -> {
                        sendToView(NoticesActivity::class.java)
                        intentIndex = 1
                        Session.updateBillingData = true
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    2 -> {
                        sendToView(AboutActivity::class.java)
                        intentIndex = 1
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    3 -> logout()
                }
            }
            items.layoutManager = LinearLayoutManager(this)
            items.adapter = adapter

            drawer_layout.closeDrawer(GravityCompat.START)
        }



    }

    private fun logout() {
        Commons.savePreference(this,"firstTime","1")
        sendToView(LoginActivity::class.java,flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        Session.reset()
    }

    override fun showMovesDetail(move: MovesModel) {
        val bundle = Bundle()
        bundle.putParcelable("move", move)
        goToView = true
        Commons.sendToViewWithBundle(this, MovesDetailActivity::class.java, bundle)

    }

}