/*
    Filter started offers to determine if the offer
    has a completed CPE portion or is attributed to kraken
    (has an offer id). How can we refactor this code for performance gains?
*/
public List<Offer> filterStartedOffers(List<Offer> startedOffers) {
    List<Offer> filteredOffers = new ArrayList<Offer>();
    APKEngagementDataSource apkEngagementDataSource = getAPKEngagementDataSource();

    for (Offer offer : startedOffers) {
        String mid = getSharedPreferences().getString(SharedPreferenceKeys.MEMBER_ID, null);
        APKEngagement apkEngagement = apkEngagementDataSource.getMemberAPKEngagementByPackageId(offer.getAndroidPackageId(), mid);
        if ((apkEngagement != null && apkEngagement.isAttributedToKraken()) || offer.hasCpeComplete()) {
            filteredOffers.add(offer);
        }
    }
    return filteredOffers;
}

/*
    In APKEngagementDataSource
*/
public APKEngagement getMemberAPKEngagementByPackageId(String packageId, String memberId) {
    Cursor cursor = database.query(APKEngagementSQLiteHelper.APK_ENGAGEMENT_TABLE,
            allColumns, APKEngagementSQLiteHelper.COLUMN_ID + " = '" + packageId + ":" + memberId + "'", null,
            null, null, null);
    try {
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            return cursorToAPKEngagement(cursor);
        } else {
            return null;
        }
    } finally {
        cursor.close();
    }
}