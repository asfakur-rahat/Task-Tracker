package com.ar.task_tracker.presentation.searchTask


import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentSearchTaskBinding
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.presentation.dialogs.ModalBottomSheet
import com.ar.task_tracker.presentation.dialogs.ModalBottomSheetListener
import com.ar.task_tracker.presentation.dialogs.getTask
import com.ar.task_tracker.presentation.dialogs.saveTask
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchTaskFragment : Fragment(R.layout.fragment_search_task), ModalBottomSheetListener {

    private lateinit var binding: FragmentSearchTaskBinding
    private val viewModel: SearchTaskViewModel by viewModels()
    private lateinit var adapter: SearchAdapter
    private var query: String? = null

    private var saveClickCounter = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchTaskBinding.bind(view)
        adapter = SearchAdapter{
            if(saveClickCounter++ == 0){
                showOptions(it)
                viewModel.viewModelScope.launch {
                    delay(500)
                    saveClickCounter = 0
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }

    private fun showOptions(it: Task) {
        saveTask(requireContext(), it)
        val bottomSheet = ModalBottomSheet.newInstance()
        bottomSheet.setTargetFragment(this, 0)
        bottomSheet.show(parentFragmentManager, ModalBottomSheet.TAG)

    }

    override fun onResume() {
        if(query != null){
            searchTask(query!!)
        }
        else{
            showNoTaskFound(emptyList())
        }
        super.onResume()
    }

    private fun completeTask(it: Task) {
        if(it.status){
            Toast.makeText(requireContext(), "The task is already completed", Toast.LENGTH_SHORT).show()
        }else{
            viewModel.markTaskAsDone(it.copy(status = true), query)
        }
    }

    private fun gotoDetails(it: Task) {
        findNavController().navigate(
            SearchTaskFragmentDirections.actionSearchTaskFragmentToTaskDetailsFragment(
                it
            )
        )
    }

    private fun gotoEdit(it: Task) {
        findNavController().navigate(
            SearchTaskFragmentDirections.actionSearchTaskFragmentToEditTaskFragment(
                it
            )
        )
    }

    private fun initObserver() {
        viewModel.loader.observe(viewLifecycleOwner){
            if(it){
                showLoader()
            }
            else{
                hideLoader()
            }
        }
        viewModel.taskList.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                showNoTaskFound(it)
            }else{
                showTaskFound(it)
            }
        }

    }

    private fun showTaskFound(it: List<Task>) {
        binding.noTaskFound.visibility = View.GONE
        binding.rvTaskList.visibility = View.VISIBLE
        adapter.submitList(it)
    }

    private fun showNoTaskFound(it: List<Task>) {
        binding.rvTaskList.visibility = View.GONE
        binding.noTaskFound.visibility = View.VISIBLE
        adapter.submitList(it)
    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE
        binding.rvTaskList.visibility = View.VISIBLE
    }

    private fun showLoader() {
        binding.rvTaskList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun initListener() {
        binding.topAppBar.setNavigationOnClickListener {
            goBack()
        }
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun initView(){
        binding.rvTaskList.visibility = View.GONE
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvTaskList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            binding.rvTaskList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding.rvTaskList.adapter = adapter
        initSearchViewHandler()
    }

    private fun initSearchViewHandler() {
        binding.searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean{
                binding.searchView.clearFocus()
                if(query != null){
                    this@SearchTaskFragment.query = query
                    searchTask(query)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun searchTask(query: String) {
        viewModel.searchTask(query)
    }

    override fun onEdit() {
        gotoEdit(getTask(requireContext())!!)
    }

    override fun onDetails() {
        gotoDetails(getTask(requireContext())!!)
    }

    override fun onCompleted() {
        completeTask(getTask(requireContext())!!)
    }

}