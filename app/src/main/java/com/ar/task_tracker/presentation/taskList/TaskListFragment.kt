package com.ar.task_tracker.presentation.taskList

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentTaskListBinding
import com.ar.task_tracker.domain.model.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment() {
    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskListAdapter

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
        //bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheet))
        adapter =
            TaskListAdapter {
                Toast.makeText(requireContext(), "clicked on task ${it.id}", Toast.LENGTH_SHORT).show()
                showOptions()
            }
        super.onViewCreated(view, savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        viewModel.taskList.observe(viewLifecycleOwner) { taskList ->
            initView(taskList)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvTaskList.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvTaskList.visibility = View.VISIBLE
            }
        }
    }

    private fun showOptions(){
        val bottomSheet  = ModalBottomSheet(
            onEdit = {
                Toast.makeText(requireContext(), "clicked on Edit", Toast.LENGTH_SHORT).show()
            },
            onDetails = {
                Toast.makeText(requireContext(), "clicked on Details", Toast.LENGTH_SHORT).show()
            },
            onCompleted ={
                Toast.makeText(requireContext(), "clicked on Done", Toast.LENGTH_SHORT).show()
            }
        )
        bottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)
    }

    private fun initView(taskList: List<Task>?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvTaskList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            binding.rvTaskList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding.rvTaskList.adapter = adapter
        adapter.submitList(taskList)
    }
}