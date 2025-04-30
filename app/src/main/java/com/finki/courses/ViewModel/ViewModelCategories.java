package com.finki.courses.ViewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.finki.courses.Model.Category;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoriesLoadedCallBack;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoryAddedCallback;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoryDeletedCallback;
import com.finki.courses.Repositories.ICategoriesRepository;
import com.finki.courses.Repositories.Implementations.CategoryRepository;

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
