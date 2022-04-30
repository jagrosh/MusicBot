/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.queue;

import java.util.List;

/**
 * @author SH1n3g4ter
 * @param <T>
 */
public interface Queue<T extends Queueable> {
    int add(T item);
    void addAt(int index, T item);
    int size();
    T pull();
    boolean isEmpty();
    List<T> getList();
    T get(int index);
    T remove(int index);
    int removeAll(long id);
    void clear();
    int shuffle(long id);
    void skip(int number);
    T moveItem(int from, int to);
}
