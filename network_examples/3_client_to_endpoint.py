'''
    Thinking through how the client (Kraken) makes a request to this endpoint,
    suggest a way to improve it.
'''
@rapid_route('/api/v1/get_offers')
@api.route('/get_offers', methods=['POST'])
@api.route('/v1/get_offers', methods=['POST'])
@lock_pcid()
def get_offers():
    ...
    '''
    Get a list of offers for this member which are then stored in a cache
    with a TTL of 1 hour
    '''
    session, member = current_app.managers.session.get_session_member(session_id)
    offers = current_app.managers.offers.get_cached_offers(session, Constants.TTL)

    response_result = {
        "offers": offers,
    }

    return jsonify(response_result)


'''
    Client code
'''

public class HomeActivity {
    public void onCreate(Bundle savedInstanceState){
        ...
    }

    public void onResume(){
        startSpinner();
        getOffers = new MCentRequest<GetOffers>(
                new GetOffers(lastUpdate, lastBalanceUpdate, referredOfferId),
                new MCentResponse.ResponseCallback() {
                    @Override
                    public void onResponse(final MCentResponse response) {
                        // displays the offers
                        stopSpinner();
                        processOfferResponse(MCentResponse response);
                },
                new MCentResponse.ErrorResponseCallback() {
                    @Override
                    public void onErrorResponse(MCentError error) {
                        stopSpinner();
                        setEmptyMessage(R.string.something_went_wrong, true);
                    }
                }
        );
        mCentApplication.logAndHandleAPIRequest(getOffers);
    }
}
