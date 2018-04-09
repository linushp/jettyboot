package cn.ubibi.jettyboot.framework.commons;

import java.util.List;

public class JBPage<T> {

    private List<T> items;
    private long itemsCount;
    private long pagesCount;
    private int pageNo;
    private int pageSize;

    public JBPage(List<T> dataList, long totalCount, int pageNo, int pageSize) {

        this.items = dataList;
        this.itemsCount = totalCount;
        this.pageNo = pageNo;
        this.pageSize = pageSize;


        this.pagesCount = totalCount / pageSize;
        if(totalCount % pageSize > 0 ){
            this.pagesCount = this.pagesCount + 1;
        }

    }


    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(long itemsCount) {
        this.itemsCount = itemsCount;
    }

    public long getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(long pagesCount) {
        this.pagesCount = pagesCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
