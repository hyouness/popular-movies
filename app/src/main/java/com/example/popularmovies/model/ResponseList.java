package com.example.popularmovies.model;

import java.util.List;

public class ResponseList<T> {
    private Integer page;

    private Integer totalResults;

    private Integer totalPages;

    private List<T> items;

    public ResponseList() {
    }

    public ResponseList(List<T> items) {
        this.items = items;
    }

    public ResponseList(Integer page, Integer totalResults, Integer totalPages, List<T> items) {
        this.page = page;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.items = items;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
