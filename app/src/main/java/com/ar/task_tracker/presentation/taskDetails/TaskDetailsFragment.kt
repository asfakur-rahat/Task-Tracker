package com.ar.task_tracker.presentation.taskDetails

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.size.Scale
import coil.size.ViewSizeResolver
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentTaskDetailsBinding
import com.ar.task_tracker.domain.model.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    private lateinit var binding: FragmentTaskDetailsBinding
    private val args: TaskDetailsFragmentArgs by navArgs()
    private lateinit var task: Task
    private val viewModel: TaskDetailsViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentTaskDetailsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        task = args.taskItem
        initView(task)
        initListener()
        initObserver()

    }

    //Listener for Buttons
    private fun initListener() {
        binding.topAppBar.setOnMenuItemClickListener {  item ->
            when(item.itemId){
                R.id.delete -> { deleteTask(task.id) }
            }
            true
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    //Call for delete task from Cloud + Room
    private fun deleteTask(id: Int) {
        viewModel.deleteTask(id, args.taskItem)
    }

    //Observers for Livedata
    private fun initObserver() {
        viewModel.deleted.observe(viewLifecycleOwner){
            if(it == true){
                goBack()
            }
        }
        viewModel.loader.observe(viewLifecycleOwner){
            if(it == true){
                showLoader()
            }else{
                hideLoader()
            }
        }
    }


    // for show and hide loader
    private fun showLoader(){
        binding.progressBar.visibility = View.VISIBLE
        binding.mainView.visibility = View.GONE
    }
    private fun hideLoader(){
        binding.progressBar.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
    }

    //Back to Task List
    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun initView(task: Task) {
        binding.tvTaskTitle.text = task.title
        binding.tvTaskDescription.text = task.description
        if(task.image == null){
            binding.ivTaskImage.visibility = View.GONE
        }else{
            binding.ivTaskImage.load(task.image){
                placeholder(R.drawable.image_placeholder)
                size(ViewSizeResolver(binding.ivTaskImage))
                scale(Scale.FIT)
            }
        }
        binding.tvStartTime.text = task.startTime
        binding.tvDeadline.text = task.deadline
        setStatus(task)
    }

    //Setting Status and Color based on task current status
    private fun setStatus(task: Task) {
        if(task.status){
            binding.tvTaskStatus.text = "Completed"
            binding.tvTaskStatus.setTextColor(Color.GREEN)
        }
        else{
            binding.tvTaskStatus.text = "Pending"
            binding.tvTaskStatus.setTextColor(Color.RED)
        }
    }


}