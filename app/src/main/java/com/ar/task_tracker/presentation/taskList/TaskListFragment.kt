package com.ar.task_tracker.presentation.taskList

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentTaskListBinding
import com.ar.task_tracker.domain.model.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

@AndroidEntryPoint
class TaskListFragment : Fragment() {
    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskListAdapter
    private var bottomSheet: ModalBottomSheet? = null
    private var saveClickCounter = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_task_list, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding = FragmentTaskListBinding.bind(view)
        adapter = TaskListAdapter {
            if(saveClickCounter++ == 0){
                showOptions(it)
                viewModel.viewModelScope.launch {
                    delay(500)
                    saveClickCounter = 0
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        viewModel.initList()
    }

    override fun onDestroyView() {
        requireActivity().supportFragmentManager.popBackStack()
        super.onDestroyView()
    }

    // Listener for buttons
    private fun initListener() {
        binding.addTaskBtn.setOnClickListener {
            findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToAddTaskFragment())
        }
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.search ->{
                    gotoSearch()
                }
            }
            true
        }
    }

    private fun gotoSearch() {
        findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToSearchTaskFragment())
    }

    //LiveDataObserver
    private fun initObserver() {
        viewModel.taskList.observe(viewLifecycleOwner) { taskList ->
            initView(taskList)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoader()
            } else {
                hideLoader()
            }
        }
    }

    // for show and hide loader
    private fun showLoader(){
        binding.progressBar.visibility = View.VISIBLE
        binding.addTaskBtn.visibility = View.GONE
        binding.mainView.visibility = View.GONE
    }
    private fun hideLoader(){
        binding.progressBar.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
        binding.addTaskBtn.visibility = View.VISIBLE
    }

    //BottomSheet Click Handler
    private fun showOptions(task: Task) {
        bottomSheet = ModalBottomSheet(
            onEdit = { gotoEdit(task) },
            onDetails = { gotoDetails(task) },
            onCompleted = { completeTask(task) }
        )
        if(bottomSheet?.isVisible == false){
            bottomSheet?.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)
        }
    }

    private fun completeTask(task: Task) {
        if(task.status){
            Toast.makeText(requireContext(), "The task is already completed", Toast.LENGTH_SHORT).show()
        }else{
            viewModel.markTaskAsDone(task.copy(status = true))
        }
    }

    //Navigation to Details Page
    private fun gotoDetails(task: Task) {
        findNavController().navigate(
            TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(
                task
            )
        )
    }
    //Navigation to Edit page
    private fun gotoEdit(task: Task) {
        findNavController().navigate(
            TaskListFragmentDirections.actionTaskListFragmentToEditTaskFragment(
                task
            )
        )
    }

    private fun initView(taskList: List<Task>?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvTaskList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            binding.rvTaskList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding.rvTaskList.adapter = adapter
        adapter.submitList(taskList)
    }
}