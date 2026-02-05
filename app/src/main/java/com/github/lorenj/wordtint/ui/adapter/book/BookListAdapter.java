package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.viewmodel.BookViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<WordBookWithSectionVO> {

    private final Context context;
    /**
     * 业务逻辑层面的WordBookWithSectionVO
     */
    private final List<WordBookWithSectionVO> wordBookEntityList = new ArrayList<>();
    /**
     * 当前正在显示的所有元素
     */
    private final List<BaseBookItem> currentBookListUI = new ArrayList<>();
    private final BookViewModel bookViewModel;
    /**
     * 二级列表贡献缓存
     */
    private final RecyclerView.RecycledViewPool tagSelectionPool;

    public BookListAdapter(Context context, BookViewModel bookViewModel) {
        this.context = context;
        this.bookViewModel = bookViewModel;
        tagSelectionPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        return currentBookListUI.get(position).getItemType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BaseBookItem.BOOK) {
            return new BookViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_parent, parent, false), context);
        } else {
            return new BookSecionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_section, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof BookViewHolder) {
            BookViewHolder bookViewHolder = (BookViewHolder) holder;
            WordBookWithSectionVO wordBookWithSectionVO = (WordBookWithSectionVO) currentBookListUI.get(position);
            bookViewHolder.bookName.setText(wordBookWithSectionVO.wordBookEntity.name);
            bookViewHolder.count.setText(String.valueOf(wordBookWithSectionVO.wordBookSectionVOList.size()));
            bookViewHolder.bookTagSelection.setRecycledViewPool(tagSelectionPool);
            // 重新加载所有的子章节
            // 标签选择初始化-保证顺序的一致性
            List<MarkColor> allSectionTag = wordBookWithSectionVO.wordBookSectionVOList
                    .stream()
                    .filter(wordBookSectionEntityVO -> wordBookSectionEntityVO.tagColor != null)
                    .map(wordBookSectionEntityVO -> wordBookSectionEntityVO.tagColor)
                    .distinct()
                    .collect(Collectors.toList());
            bookViewHolder.tagSelectionListAdapter.replaceAll(allSectionTag);
        }
        if (holder instanceof BookSecionViewHolder) {
            BookSecionViewHolder bookSecionViewHolder = (BookSecionViewHolder) holder;
            WordBookSectionVO wordBookSectionEntity = (WordBookSectionVO) currentBookListUI.get(position);
            bookSecionViewHolder.sectionTextView.setText(wordBookSectionEntity.wordBookSectionEntity.name);
            bookSecionViewHolder.elementCount.setText(String.valueOf(wordBookSectionEntity.elementCount));
            if (wordBookSectionEntity.selection) {
                bookSecionViewHolder.bookSectionSelection.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_plan));
            } else {
                bookSecionViewHolder.bookSectionSelection.setImageDrawable(null);
            }
            // tag标签颜色
            if (wordBookSectionEntity.tagColor != null) {
                Drawable drawable = bookSecionViewHolder.bookTag.getDrawable();
                drawable = drawable.mutate();
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, wordBookSectionEntity.tagColor.getMapColorID()));
            }
        }
        Log.d("BookListAdapter", String.valueOf(position));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads);
        for (Object payload : payloads) {
            if (payload == Item.CLICK_SECTION) {
                BookSecionViewHolder bookSecionViewHolder = (BookSecionViewHolder) holder;
                WordBookSectionVO wordBookSectionEntity = (WordBookSectionVO) currentBookListUI.get(position);
                bookSecionViewHolder.sectionTextView.setText(wordBookSectionEntity.wordBookSectionEntity.name);
                bookSecionViewHolder.elementCount.setText(String.valueOf(wordBookSectionEntity.elementCount));
                if (wordBookSectionEntity.selection) {
                    bookSecionViewHolder.bookSectionSelection.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_plan));
                } else {
                    bookSecionViewHolder.bookSectionSelection.setImageDrawable(null);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return currentBookListUI.size();
    }

    @Override
    public void replaceAll(Collection<WordBookWithSectionVO> newWordList) {
        List<BaseBookItem> newList = flatten(new ArrayList<>(newWordList));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookDiffCallback(this.currentBookListUI, newList));
        this.currentBookListUI.clear();
        this.currentBookListUI.addAll(newList);
        if (wordBookEntityList.isEmpty()) wordBookEntityList.addAll(newWordList);
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * 批量选择章节
     */
    public void batchSelectSection(int bookId, MarkColor selectMarkColor) {
        wordBookEntityList.stream()
                .filter(wordBookWithSectionVO -> wordBookWithSectionVO.wordBookEntity.id == bookId)
                .findFirst()
                .map(wordBookWithSectionVO -> wordBookWithSectionVO.wordBookSectionVOList)
                .orElse(new ArrayList<>())
                .stream()
                .filter(wordBookSectionVO -> wordBookSectionVO.tagColor == selectMarkColor)
                .forEach(wordBookSectionVO -> {
                    bookViewModel.selectSection(wordBookSectionVO.wordBookSectionEntity);
                    wordBookSectionVO.selection = !wordBookSectionVO.selection;
                });
        replaceAll(new ArrayList<>(wordBookEntityList));
    }

    /**
     * 将元数据转为扁平数据<br>
     * 必须深克隆
     */
    public List<BaseBookItem> flatten(List<WordBookWithSectionVO> wordBookWithSectionVOList) {
        List<BaseBookItem> result = new ArrayList<>();
        for (WordBookWithSectionVO bookVO : wordBookWithSectionVOList) {
            result.add(bookVO);
            if (!bookVO.folded) {
                for (WordBookSectionVO sectionVO : bookVO.wordBookSectionVOList) {
                    WordBookSectionVO clonedSection = new WordBookSectionVO(sectionVO.wordBookSectionEntity);
                    clonedSection.selection = sectionVO.selection;
                    clonedSection.elementCount = sectionVO.elementCount;
                    clonedSection.tagColor = sectionVO.tagColor;
                    result.add(clonedSection);
                }
            }
        }
        return result;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RelativeLayout bookItem;
        private final TextView bookName, count;
        private final RecyclerView bookTagSelection;
        private TagSelectionListAdapter tagSelectionListAdapter;

        public BookViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.bookName = itemView.findViewById(R.id.tv_item_book);
            this.count = itemView.findViewById(R.id.tv_item_book_count);
            this.bookItem = itemView.findViewById(R.id.rl_item_book);
            this.bookTagSelection = itemView.findViewById(R.id.rv_book_tag_selection);

            bookItem.setOnClickListener(this);
            LinearLayoutManager tagSelectionLm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            bookTagSelection.setLayoutManager(tagSelectionLm);
            // 当标签选择列表点击了一个标签后会回调,这里用回调而不是viewModel
            tagSelectionListAdapter = new TagSelectionListAdapter(context, markColor -> {
                BookListAdapter bookListAdapter = (BookListAdapter) getBindingAdapter();
                if (bookListAdapter == null) return;
                BaseBookItem baseBookItem = bookListAdapter.currentBookListUI.get(getBindingAdapterPosition());
                bookListAdapter.batchSelectSection(((WordBookWithSectionVO) baseBookItem).wordBookEntity.id, markColor);
            });
            bookTagSelection.setAdapter(tagSelectionListAdapter);
        }

        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.rl_item_book) {
                int position = getBindingAdapterPosition();
                BookListAdapter bookListAdapter = (BookListAdapter) getBindingAdapter();
                if (bookListAdapter == null) return;
                WordBookWithSectionVO wordBookWithSectionVO = (WordBookWithSectionVO) bookListAdapter.currentBookListUI.get(position);
                wordBookWithSectionVO.folded = !wordBookWithSectionVO.folded;
                bookListAdapter.replaceAll(bookListAdapter.wordBookEntityList);
            }
        }
    }

    public static class BookSecionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View itemView;
        private final TextView sectionTextView;
        private final TextView elementCount;
        private final ImageView bookSectionSelection, bookTag;

        public BookSecionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.sectionTextView = itemView.findViewById(R.id.tv_item_book_section);
            this.bookSectionSelection = itemView.findViewById(R.id.iv_item_book_section_selection);
            this.elementCount = itemView.findViewById(R.id.tv_item_book_section_count);
            this.bookTag = itemView.findViewById(R.id.iv_item_book_tag);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;
            BookListAdapter bindingAdapter = (BookListAdapter) getBindingAdapter();
            if (bindingAdapter == null) return;
            // 传递选中的id数据
            WordBookSectionVO selectWordBookSectionVO = (WordBookSectionVO) bindingAdapter.currentBookListUI.get(position);
            selectWordBookSectionVO.selection = !selectWordBookSectionVO.selection;
            for (WordBookWithSectionVO wordBookWithSectionVO : bindingAdapter.wordBookEntityList) {
                for (WordBookSectionVO wordBookSectionVO : wordBookWithSectionVO.wordBookSectionVOList) {
                    if (wordBookSectionVO.wordBookSectionEntity.id == selectWordBookSectionVO.wordBookSectionEntity.id) {
                        selectWordBookSectionVO = wordBookSectionVO;
                        break;
                    }
                }
            }
            // 必须修改元数据的值
            bindingAdapter.bookViewModel.selectSection(selectWordBookSectionVO.wordBookSectionEntity);
            selectWordBookSectionVO.selection = !selectWordBookSectionVO.selection;
            bindingAdapter.notifyItemChanged(position, Item.CLICK_SECTION);
        }
    }

    private enum Item {
        CLICK_BOOK,
        CLICK_SECTION
    }

}
