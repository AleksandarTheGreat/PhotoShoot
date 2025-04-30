package com.finki.courses.ViewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.finki.courses.Fragments.FragmentHelpers.FragmentHomeHelper;
import com.finki.courses.Model.Category;
import com.finki.courses.Repositories.Callbacks.OnCategoriesLoadedCallBack;
import com.finki.courses.Repositories.Callbacks.OnCategoryAddedCallback;
import com.finki.courses.Repositories.Callbacks.OnCategoryDeletedCallback;
import com.finki.courses.Repositories.ICategoriesRepository;
import com.finki.courses.Repositories.Implementations.CategoryRepository;
import com.finki.courses.databinding.FragmentHomeBinding;

import java.util.List;

public class ViewModelCategories extends ViewModel implements ICategoriesRepository {

    private Context context;
    private CategoryRepository categoryRepository;
    private MutableLiveData<List<Category>> mutableLiveDataCategories;
    public void init(Context context){
        this.context = context;
        this.categoryRepository = new CategoryRepository(context);
        this.mutableLiveDataCategories = new MutableLiveData<>();
    }

    @Override
    public void listAll(OnCategoriesLoadedCallBack onCategoriesLoadedCallBack) {
        categoryRepository.listAll(onCategoriesLoadedCallBack);
    }

    @Override
    public Category findCategoryById(long id) {
        return categoryRepository.findCategoryById(id);
    }

    @Override
    public void add(String name, OnCategoryAddedCallback onCategoryAddedCallback) {
        categoryRepository.add(name, onCategoryAddedCallback);
    }

    @Override
    public void deleteById(long id, OnCategoryDeletedCallback onCategoryDeletedCallback) {
        categoryRepository.deleteById(id, onCategoryDeletedCallback);
    }

    public MutableLiveData<List<Category>> getMutableLiveDataCategories() {
        return mutableLiveDataCategories;
    }

    public void setMutableLiveDataCategories(MutableLiveData<List<Category>> mutableLiveDataCategories) {
        this.mutableLiveDataCategories = mutableLiveDataCategories;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }
}
