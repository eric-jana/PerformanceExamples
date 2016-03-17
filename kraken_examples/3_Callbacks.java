/*
	Assume that both of these callbacks happen on the main thread.
	Explain how we can improve the performance of both these 
	callbacks.
*/

MCentRequest getOffers = new MCentRequest<GetOffers>(
        new GetOffers(lastUpdate, lastBalanceUpdate, referredOfferId),
        new MCentResponse.ResponseCallback() {
            @Override
            public void onResponse(final MCentResponse response) {
                Log.d(TAG, "started offers response processing");
                final GetOffersResponse offersResponse = (GetOffersResponse) response.getApiResponse();
                final List<Fragment> allFragments = tabsManager.getAllFragments();

                final List<Offer> availableOffers = mCentApplication.getAvailableOffers(offersResponse);
                final List<Offer> startedOffers = mCentApplication.getStartedOffers(offersResponse);

                mCentApplication.getShareManager().storeReferralData(offersResponse.getReferralData());
                dailyBonusHelper.storeDailyBonusData(offersResponse.getDailyBonusData());
                mCentApplication.getMessagingCompensationHelper().storeData(offersResponse.getMessagingCompensationData());

                for (Fragment fragment : allFragments) {
                    if (fragment instanceof NewAppsGridPageFragment) {
                        ((OffersSwipeRefreshPageFragment) fragment).populateRecyclerView(availableOffers, startedOffers);
                    }
                    if (fragment instanceof InProgressPageFragment) {
                        ((OffersSwipeRefreshPageFragment) fragment).populateRecyclerView(startedOffers);
                    }
                }

                addAndCountRefreshesIn24Hours();
            }
        },
        new MCentResponse.ErrorResponseCallback() {
            @Override
            public void onErrorResponse(MCentError error) {
                setEmptyMessage(R.string.something_went_wrong, true);
            }
        }
);


MCentRequest fetchActivityFeed = new MCentRequest<>(
        new ActivityFeedRequest(),
        new MCentResponse.ResponseCallback() {
            @Override
            public void onResponse(final MCentResponse response) {
                ActivityFeedResponse activityFeedResponse = (ActivityFeedResponse) response.getApiResponse();
                final List<ActivityFeedItem> activityFeedItems = activityFeedResponse.getActivityFeedItems();
                final List<ActivityFeedItem> filteredItems = filterActivityFeedItems(activityFeedItems);

                addFeedbackAskItemsToSet(filteredItems);
                activityFeedCounterManager.fireServedCounters(activityFeedItems);
                serializeActivityFeedItems(filteredItems);
                
                addAndCountRefreshesIn24Hours();

                setNewItems(filteredItems);
                showEmptyMessage(filteredItems);
                populateActivityFeed(filteredItems);
                hideShowToolbarListener.onRefresh();
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

        },
        new MCentResponse.ErrorResponseCallback() {
            @Override
            public void onErrorResponse(MCentError error) {
                mCentApplication.getToastHelper().showGenericNoInternetToast(activity);
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
);

application.logAndHandleAPIRequest(getOffers);
application.logAndHandleAPIRequest(fetchActivityFeed);

...

public void addAndCountRefreshesIn24Hours(){
	Set<String> homeRefreshedAt = sharedPreferences.getStringSet(
    	SharedPreferenceKeys.HOME_REFRESHED_AT, new HashSet<String>()
    );

	// modifies the String set to remove values older than 24 hours
    removeOldValuesFromSet(homeRefreshedAt);

    homeRefreshedAt.add(System.currentTimeMillis());
    sharedPreferences.putStringSet(
    	SharedPreferenceKeys.HOME_REFRESHED_AT, homeRefreshedAt
    )

    // fires a counter if there have been 
	// more than X number of refreshes per day
    countNumRefreshesInOneDay(homeRefreshedAt);
}
...

