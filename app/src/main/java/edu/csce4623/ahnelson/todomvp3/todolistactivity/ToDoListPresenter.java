package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import java.util.List;

import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.addedittodoitem.AddEditToDoItemActivity;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItemRepository;
import edu.csce4623.ahnelson.todomvp3.data.ToDoListDataSource;

/**
 * ToDoListPresenter -- Implements the Presenter interface from ToDoListContract Presenter
 */
public class ToDoListPresenter extends ToDoListFragment implements ToDoListContract.Presenter {

    //Data repository instance
    //Currently has a memory leak -- need to refactor context passing
    public static ToDoItemRepository mToDoItemRepository;
    //View instance
    private final ToDoListContract.View mToDoItemView;

    private ToDoListFragment mtoDoListFragment;
    // Integer request codes for creating or updating through the result method
    public static final int CREATE_TODO_REQUEST = 0;
    public static final int UPDATE_TODO_REQUEST = 1;

    /**
     * ToDoListPresenter constructor
     * @param toDoItemRepository - Data repository instance
     * @param toDoItemView - ToDoListContract.View instance
     */
    public ToDoListPresenter(@NonNull ToDoItemRepository toDoItemRepository, @NonNull ToDoListContract.View toDoItemView, @NonNull ToDoListFragment toDoListFragment){
        mToDoItemRepository = toDoItemRepository;
        mToDoItemView = toDoItemView;
        //Make sure to pass the presenter into the view!
        mToDoItemView.setPresenter(this);
        mtoDoListFragment = toDoListFragment;
    }

    @Override
    public void start(){
        //Load all toDoItems
        loadToDoItems();
    }


    @Override
    public void addNewToDoItem() {
        //Create stub ToDoItem with temporary data
        ToDoItem item = new ToDoItem();
        item.setCompleted(false);
        item.setDueDate(System.currentTimeMillis());
        item.setId(-1);
        //Show AddEditToDoItemActivity with a create request and temporary item
        mToDoItemView.showAddEditToDoItem(item,CREATE_TODO_REQUEST);
    }

    @Override
    public void showExistingToDoItem(ToDoItem item) {
        //Show AddEditToDoItemActivity with a edit request, passing through an item
       Log.d("ToDoListPresenter", "TODO: Show Existing ToDoItem");
       mToDoItemView.showAddEditToDoItem(item,UPDATE_TODO_REQUEST);
    }

    @Override
    public void result(int requestCode, int resultCode, ToDoItem item) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATE_TODO_REQUEST){
                createToDoItem(item);
                requestCode = UPDATE_TODO_REQUEST;
                Log.d("RESULT", "inside result");
            }else if(requestCode == UPDATE_TODO_REQUEST){
                updateToDoItem(item);
            }else{
                Log.e("ToDoPresenter", "No such request!");
            }
        }
    }

    @Override
    public void deleteToDoItem(ToDoItem toDoItem) {
        try{
            mToDoItemRepository.deleteToDoItem(toDoItem.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Create ToDoItem in repository from ToDoItem and reload data
     * @param item - item to be placed in the data repository
     */
    private void createToDoItem(ToDoItem item){
        try{
            mToDoItemRepository.createToDoItem(item);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Update ToDoItem in repository from ToDoItem and reload data
     * @param item -- ToDoItem to be updated in the ToDoItemRepository
     */
    @Override
    public void updateToDoItem(ToDoItem item){
        try{
            Log.d("ToDoListPresenter", "TODO: Update Item");
            mToDoItemRepository.saveToDoItem(item);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * loadToDoItems -- loads all items from ToDoItemRepository
     * Two callbacks -- success/failure
     */
    @Override
    public void loadToDoItems(){
        Log.d("ToDoListPresenter","Loading ToDoItems");
        mToDoItemRepository.getToDoItems(new ToDoListDataSource.LoadToDoItemsCallback() {
            @Override
            public void onToDoItemsLoaded(List<ToDoItem> toDoItems) {
                Log.d("PRESENTER","Loaded");
                if (toDoItems.size() == 0){
                    ToDoItem temp = new ToDoItem();
                    temp.setId(-1);
                    temp.setDueDate(-1);
                    temp.setCompleted(false);
                    temp.setTitle("Temporary To-Do Item");
                    temp.setContent("Temporary Content");
                    toDoItems.add(temp);
                }
                mToDoItemView.showToDoItems(toDoItems);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER","Not Loaded");
            }
        });
    }
}
