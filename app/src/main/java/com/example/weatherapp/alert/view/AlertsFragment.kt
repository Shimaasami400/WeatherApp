package com.example.weatherapp.alert.view

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weatherapp.AlertState
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.alert.viewmodel.AlertViewModel
import com.example.weatherapp.alert.viewmodel.AlertViewModelFactory
import com.example.weatherapp.databinding.AlertDialogBinding
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.db.DatabaseClient
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.helper.setDate
import com.example.weatherapp.helper.setTime
import com.example.weatherapp.model.AlertKind
import com.example.weatherapp.model.LocationLatLngPojo
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.WeatherRemoteDataSourceImp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit


private const val CHANNEL_ID = "my_channel_id"
private const val NOTIFICATION_ID = 1

class AlertsFragment : Fragment() {

    private lateinit var _binding: FragmentAlertsBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var customAlertDialogBinding: AlertDialogBinding
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder


    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isNotificationPermissionGranted = notificationManager.areNotificationsEnabled()
        if (!isNotificationPermissionGranted) {
            showNotificationPermissionDialog()
        }
        alertViewModelFactory = AlertViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(RetrofitHelper.weatherApiService),
                WeatherLocalDataSourceImp.getInstance(DatabaseClient.getInstance(requireContext()).weatherDataBaseDao())
            ))


        alertViewModel = ViewModelProvider(this,alertViewModelFactory)[AlertViewModel::class.java]
        customAlertDialogBinding = AlertDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())

        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener{
            /*if (NetworkConnectivity.getInstance(requireActivity().application).isOnline())
            {*/
                val action = AlertsFragmentDirections.actionAlertsFragment2ToMapFragment()
                action.type = Constants.ALERT_KEY
                Navigation.findNavController(it).navigate(action)
            /*}else
            {
                Snackbar.make(requireView(), getString(R.string.check_your_connection), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.dismiss)) {
                    }.show()
            }*/
        }

        val adapter = AlertsAdapter(AlertsAdapter.RemoveClickListener {
            delAlertSnack(it)
        })
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager

        alertViewModel.getAlertWeather()
        lifecycleScope.launch(Dispatchers.Main) {

            alertViewModel.alertList.collectLatest {
                when (it) {
                    is AlertState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AlertState.Success -> {
                        adapter.submitList(it.data)
                        if (it.data.isNotEmpty()) {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.groupNoAlarms.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.groupNoAlarms.visibility = View.VISIBLE
                        }
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.groupNoAlarms.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val args = AlertsFragmentArgs.fromBundle(requireArguments())
        val latLng = args.latlon
        if (latLng != null)
        {
            launchCustomAlertDialog(latLng)
        }
    }

    private fun delAlertSnack(alertPojo: AlertPojo) {
        Snackbar.make(requireView(), getString(R.string.ask_del_fav), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.del_item)) {
                alertViewModel.deleteWeatherAlert(alertPojo)
            }.show()
    }
    private fun launchCustomAlertDialog(locationLatLngPojo: LocationLatLngPojo) {

        val alertDialog = materialAlertDialogBuilder.setView(customAlertDialogBinding.root)
            .setBackground(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.background, requireActivity().theme
                )
            ).setCancelable(false).show()
        setTimeAndDateInDialog()


        var startTime = Calendar.getInstance().timeInMillis
        val endCal = Calendar.getInstance()
        endCal.add(Calendar.DAY_OF_MONTH, 0)
        endCal.add(Calendar.HOUR,1)
        var endTime = endCal.timeInMillis

        customAlertDialogBinding.buttonSave.setOnClickListener {
            val id = if (customAlertDialogBinding.radioAlarm.isChecked) {
                saveToDatabase(startTime, endTime, AlertKind.ALARM,locationLatLngPojo)
            } else {
                saveToDatabase(startTime, endTime, AlertKind.NOTIFICATION,locationLatLngPojo)
            }

            scheduleWork(startTime, endTime, id)
            checkDisplayOverOtherAppPerm()

            alertDialog.dismiss()
        }
        customAlertDialogBinding.cardViewChooseEnd.setOnClickListener {
            setAlarm(endTime) { currentTime ->
                endTime = currentTime
                customAlertDialogBinding.textViewEndDate.setDate(currentTime)
                customAlertDialogBinding.textViewEndTime.setTime(currentTime)
            }
        }
        customAlertDialogBinding.buttonCancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    private fun saveToDatabase(startTime: Long, endTime: Long, alarmKind: String,locationLatLngPojo: LocationLatLngPojo): String {
        val alertPojo =
            AlertPojo(start = startTime, end = endTime, kind = alarmKind, lat = locationLatLngPojo.lat, lon = locationLatLngPojo.lng)
        alertViewModel.insertWeatherAlert(alertPojo)
        Log.i("TAG", "saveToDatabase: =====${alertPojo.id}")
        return alertPojo.id
    }

    private fun checkDisplayOverOtherAppPerm() {
        if (!Settings.canDrawOverlays(requireActivity())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireActivity().packageName)
            )
            someActivityResultLauncher.launch(intent)
        }
    }
    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Settings.canDrawOverlays(requireContext())) {
//                Snackbar.make(
//                    binding.root,
//                    getString(R.string.The_alarm_may_be_not_work_as_expected),
//                    LENGTH_LONG
//                ).setAction("Enable") {
//                    sendToEnableIt()
//                }.show()
            }
        }


    private fun scheduleWork(startTime: Long, endTime: Long, tag: String) {

        val _Day_TIME_IN_MILLISECOND = 24 * 60 * 60 * 1000L
        val timeNow = Calendar.getInstance().timeInMillis

        val inputData = Data.Builder()
        inputData.putString(ID, tag)


        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val myWorkRequest: WorkRequest = if ((endTime - startTime) < _Day_TIME_IN_MILLISECOND) {
            Log.d("TAG", "scheduleWork: one")
            OneTimeWorkRequestBuilder<AlertWorker>().addTag(tag).setInitialDelay(
                startTime - timeNow, TimeUnit.MILLISECONDS
            ).setInputData(
                inputData = inputData.build()
            ).setConstraints(constraints).build()

        } else {
            Log.i("TAG", "scheduleWork:  else ")
            Log.i("TAG", "scheduleWork: ${endTime - timeNow}")
            Log.i("TAG", "scheduleWork: ${startTime - timeNow}")
            WorkManager.getInstance(requireContext()).enqueue(
                OneTimeWorkRequestBuilder<AlertWorker>().addTag(tag).setInitialDelay(
                    endTime - timeNow, TimeUnit.MILLISECONDS
                ).setInputData(
                    inputData = inputData.build()
                ).setConstraints(constraints).build()
            )
            PeriodicWorkRequest.Builder(
                AlertWorker::class.java, 24L, TimeUnit.HOURS, 1L, TimeUnit.HOURS
            ).addTag(tag).setInputData(
                inputData = inputData.build()
            ).setConstraints(constraints).build()
        }
        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
    }
    private fun setTimeAndDateInDialog() {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        Log.i("TAG", "setTimeAndDateInDialog: ${currentTime}")
        val timeAfterOneHour = calendar.get(Calendar.HOUR_OF_DAY)
        calendar.set(Calendar.HOUR_OF_DAY, timeAfterOneHour + 1)//+2
        customAlertDialogBinding.textViewEndDate.setDate(calendar.timeInMillis)
        customAlertDialogBinding.textViewEndTime.setTime(calendar.timeInMillis)
    }

    private fun setAlarm(minTime: Long, callback: (Long) -> Unit) {
       val color = ResourcesCompat.getColor(resources, R.color.rose, requireActivity().theme)
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            val datePickerDialog = DatePickerDialog(
                requireContext(), R.style.DialogTheme, { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    val timePickerDialog = TimePickerDialog(
                        requireContext(), R.style.DialogTheme, { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                        }, this.get(Calendar.HOUR_OF_DAY), this.get(Calendar.MINUTE), false
                    )
                    timePickerDialog.show()
                    timePickerDialog.setCancelable(false)
//                    timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(color)
//                    timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(color)
                },

                this.get(Calendar.YEAR), this.get(Calendar.MONTH), this.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.datePicker.minDate = minTime
            datePickerDialog.show()
            datePickerDialog.setCancelable(false)
//            datePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(color)
//            datePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(color)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    private fun sendNotification()
    {
        val imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.alert)

        val builder = NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(imageBitmap)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        with(NotificationManagerCompat.from(requireActivity()))
        {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID,builder.build())
        }

    }
    private fun showNotificationPermissionDialog()
    {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.notification_permission))
            .setMessage(getString(R.string.notification_permmission_body))
            .setPositiveButton(getString(R.string.permission_request)) { dialog: DialogInterface, _: Int ->
                openAppSettings()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dismiss)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = alertDialogBuilder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rose))
            negativeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rose))

            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 16, 0)
            negativeButton.layoutParams = params
        }
        dialog.show()
    }
    private fun openAppSettings()
    {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}