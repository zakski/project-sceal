package com.szadowsz.gospel.core.listener;

import com.szadowsz.gospel.core.listener.PrologListener;
import com.szadowsz.gospel.core.event.interpreter.LibraryEvent;
import com.szadowsz.gospel.core.event.interpreter.QueryEvent;
import com.szadowsz.gospel.core.event.interpreter.TheoryEvent;

public class TestPrologEventListener implements PrologListener {
    public String firstMessage = "";
    public String secondMessage = "";

    @Override
    public void theoryChanged(TheoryEvent ev) {
    	firstMessage = ev.getOldTheory().toString();
    	secondMessage = ev.getNewTheory().toString();
    }

    @Override
    public void newQueryResultAvailable(QueryEvent ev) {
    	firstMessage = ev.getSolution().getQuery().toString();
    	secondMessage = ev.getSolution().toString();
    }

    @Override
    public void libraryLoaded(LibraryEvent ev) {
    	firstMessage = ev.getLibraryName();
    }

    @Override
    public void libraryUnloaded(LibraryEvent ev) {
    	firstMessage = ev.getLibraryName();
    }
}