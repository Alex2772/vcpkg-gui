package ru.alex2772.vcpkggui.util;

import java.util.*;

/**
 * Implementation like Apache Lazy List for big data structure to prevent UI lag.
 * @param <T>
 */
public class LazyList<T> extends ArrayList<T> {

    public interface IStreamObjectProvider<T>  {

        /**
         * @return estimated list size for efficient memory allocation
         */
        int estimateListSize();


        /**
         * Reads next element from the stream.
         * @param index element index
         * @return next element from the stream.
         */
        T readElement(int index);

        /**
         * Moves "the cursor" to the specified element.
         * @param index element index
         */
        void seekToElement(int index);
    }

    /**
     * Index of element which will be read.
     */
    private int mToReadElementIndex = 0;

    private final IStreamObjectProvider<T> mDataProvider;

    private LazyList(int capacity, IStreamObjectProvider<T> provider) {
        super(capacity);
        mDataProvider = provider;

        // make size same as capacity
        for (int i = 0; i < capacity; ++i) {
            add(null);
        }
    }


    public static <T> LazyList<T> create(IStreamObjectProvider<T> provider) {
        return new LazyList<T>(provider.estimateListSize(), provider);
    }


    @Override
    public T get(int index) {
        T object = super.get(index);
        if (object == null) {
            if (mToReadElementIndex == index) {
                // move the cursor to the element before reading it
                mDataProvider.seekToElement(index);
                mToReadElementIndex = index;
            }
            T element = mDataProvider.readElement(index);
            set(index, element);
            ++mToReadElementIndex;
            return element;
        }
        return object;
    }
}
