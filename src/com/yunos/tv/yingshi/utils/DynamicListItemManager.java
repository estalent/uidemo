package com.yunos.tv.yingshi.utils;

import java.util.List;
import java.util.LinkedList;
import android.util.Log;
import android.util.Pair;

/**
 * A simple resolution of dynamic list (regardless of the view style) items Cache Management
 * With paging caching method,  and support the specified cache page count and items count per page.
 */
public class DynamicListItemManager<T> {
	private static final int minlistsCount = 3;
	
	private int totalCount = 0;
	private int pageSize = 0;
	
	private LinkedList<Pair<Integer, List<T>>> cacheListPairs = null;
	private int maxListPairCount = 0;
	
	private boolean isRequesting = false;
	private int currRequestPageNo = -1;
	
	private ItemListener listener;

	/**
	 * ItemListener接口
     * ItemListener, we need 'requestItems' or 'cancelRequest' by this
     */
	public interface ItemListener{
		/**
	     * request items by the specific pageNo, if cann't find them in cache
	     * @param pageNo, the specific page number
	     */
		void requestItems(int pageNo);
		/**
	     * cancel request, When the request is not necessary
	     */
		void cancelRequest();
	}

    /**
     * DynamicListItemManager构造器
     *
     * @param totalCount, total items count of the list, Only pass the current count
     * @param pageSize, number count per page
     * @param cachePageCout, number count pages you want to cache
     * @param listener, interface of ItemListener, you need pass, cann't be null
     */
	@SuppressWarnings("unchecked")
	public DynamicListItemManager(int totalCount, int pageSize, int cachePageCout, ItemListener listener){
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.listener = listener;
		maxListPairCount = cachePageCout > minlistsCount ? cachePageCout : minlistsCount;
		cacheListPairs = new LinkedList<Pair<Integer, List<T>>>();
	}
	
	 /**
     * Check is Requesting new page
     * 检测是否正在请求新页数据
     * @return true or false
     */
	public boolean getIsRequesting(){
		return isRequesting;
	}
	
	/**
	* 获取当前的总数
	* Get the current total count we know
	* @return the count
	*/	
	public int getTotalCount(){
		return totalCount;
	}
	
	/**
	* 获取具体位置的Item项
	* Get the specific position item
	* @param position, the position
	* @return the generic object
	*/		
	public T getItem(int position){
		T item = null;
		if ( position < 0 ){
			return null;
		}
		int pageNo = position/pageSize;
		
		item = getItemInLists(position);
		if ( item != null ){
			//logI("getItem: get in cache list, position=" + position);
			return item;
		}
		
		if ( isRequesting ){
			//logI("getItem: isRequesting currRequestPageNo = " + currRequestPageNo + " ,pageNo=" + pageNo);
			if ( pageNo == currRequestPageNo ){
				//logI("getItem: isRequesting currRequestPageNo");
				return null;
			}else{
				//logI("getItem: isRequesting and need cancelRequest");
				listener.cancelRequest();
				isRequesting = false;
			}
		}
			

		//logI("getItem: need requestItems: pageNo=" + pageNo);
		currRequestPageNo = pageNo;			
		isRequesting = true;
		listener.requestItems(currRequestPageNo);
		return null;
		
	}
	
	/**
	* response item by List<T> type, you need call the method in your code, after requested items
	* @param List<T> list, the item list
	* @param pageNo, the page number of the list
	*/	
	public synchronized void responseItems(List<T> list, int pageNo){
		if ( list == null || pageNo < 0 )
			return;
		
		if ( searchPageNo(pageNo) != null )
			return;
		
		LinkedList<Pair<Integer, List<T>>> cacheList = cacheListPairs;
		int cacheListCount = cacheList.size();
		Pair<Integer, List<T>> pair = new Pair(pageNo, list);
		
		//logI("responseItems: cacheListCount="+cacheListCount);
		if ( cacheListCount <= 0 ){	
			//logI("responseItems: cache the first list");
			cacheList.add(pair);
		}else{
			int min = cacheList.get(0).first;
			int max = cacheList.get(cacheListCount-1).first;
			
			if ( pageNo < min ){
				//Need rollback, Abandon the max pageNo, if cache is full
				if ( cacheListCount >= maxListPairCount ){
					//logI("responseItems: pageNo < min, removeLast");
					cacheList.removeLast();
				}
				//logI("responseItems: addFirst");
				cacheList.addFirst(pair);
			}else if ( pageNo > max ){
				//Need rollforward, Abandon the min pageNo, if cache is full
				if ( cacheListCount >= maxListPairCount ){
					//logI("responseItems: pageNo > min, removeFirst");
					cacheList.removeFirst();
				}
				//logI("responseItems: addLast");
				cacheList.addLast(pair);
			}else{
				//Abandon the Farthest pageNo, if cache is full
				if ( cacheListCount >= maxListPairCount ){
					int d1 = cacheList.get(0).first - pageNo;
					int d2 = cacheList.get(cacheListCount-1).first - pageNo;
					//logI("responseItems: d1="+d1+" d2="+d2);
					if ( Math.abs(d1) > Math.abs(d2) ){
						//logI("responseItems: removeFirst");
						cacheList.removeFirst();
					}else{
						//logI("responseItems: removeLast");
						cacheList.removeLast();
					}
				}
				int location = locatePageNo(pageNo);
				//logI("responseItems: add location="+location);
				if ( location < 0 || location > cacheList.size() ){
					logE("responseItems: locatePageNo Error: location="+location);
				}
				cacheList.add(location, pair);
			}
		}
		
		int count  = pageNo*pageSize + list.size();
		logI("responseItems: pageNo="+pageNo+" size="+list.size());
		if ( totalCount < count )
			totalCount = count;

		isRequesting = false;
		
	}
	
	/**
	* check the specific position item in Cache or not
	* @param position, the position
	* @return true or false
	*/		
	public boolean checkInCache(int position){
		return getItemInLists(position) == null ? false : true;
	}
	
	private int locatePageNo(int pageNo){
		LinkedList<Pair<Integer, List<T>>> cacheList = cacheListPairs;
		int low = 0, high, mid = -1;
		int iPageNo;
		
        high = cacheList.size() - 1;
        
        //Guess equal, generally be right
        if ( high >= 0 ){
        	int index = pageNo - cacheList.get(0).first; 	
        	
        	if ( index >= 0 && index <= high ){
        		iPageNo = cacheList.get(index).first;
        		if ( iPageNo == pageNo ){
        			return index;
        		} else if ( iPageNo > pageNo ){
        			high = index - 1;
        		} else {
        			low = index + 1;
        		}      		
        	}
        }
            
		while (low <= high){
			mid = (low + high) / 2;
			iPageNo = cacheList.get(mid).first;
			if ( iPageNo < pageNo ){
			    low = mid + 1;
			} else if ( iPageNo > pageNo ){
			    high = mid - 1;
			} else{
			    return mid;
			}
		}
		
		if ( mid >= 0 ){
			if (cacheList.get(mid).first < pageNo ){
			    return mid+1;
			} else {
			    return mid;
			}
		}
		
		return mid;
	}
	
	/**
	 * @param pageNo
	 * @return
	 */
	private Pair<Integer, List<T>> searchPageNo(int pageNo){
		LinkedList<Pair<Integer, List<T>>> cacheList = cacheListPairs;
        int high = cacheList.size() - 1;  
        
        int location = locatePageNo(pageNo);
        if ( location < 0 || location > high || cacheList.get(location).first != pageNo ){
        	return null;
        }
        	
        return cacheList.get(location);
	}
	
	/**
	 * 
	 * @param position
	 * @return
	 */
	private T getItemInLists(int position){
		int pageNo = position/pageSize;
		
		Pair<Integer, List<T>> pair = searchPageNo(pageNo);
		if ( pair == null )
			return null;
		
		int index = pair.first*pageSize;
		if ( position - index < pair.second.size() )
			return pair.second.get(position-index);
		
		return null;
	}
	
	private void logI(String msg){
		//Log.i("DynamicListItemManager", msg);
	}
	
	private void logE(String msg){
		Log.e("DynamicListItemManager", msg);
	}
	
}
