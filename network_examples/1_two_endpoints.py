"""
    Let’s say we have concurrent requests from Kraken that hit these 2 endpoints.
"""
@api.route('/v1/get_offers')
@lock_pcid()
def get_offers():
	params = request.json
    kraken_version = params.get("kraken_version")
    session_id = (
    	params.get('auth_token') or
    	params.get("session_id")
    )

    session_member = session_manager.get_session_member(
    	session_id
    )
    offers = offer_manager.get_offers(
    	session_member
    )

    info = kraken_configurations.get_info_for_member(
    	session_member
    )

    return jsonify({
    	'configuration': info,
    	'offers': offers
    })

@api.route('/v1/get_balance', methods=['POST'])
@lock_pcid()
def get_balance():
    params = request.json
    session_id = (
    	params.get('auth_token') or
    	params.get("session_id")
    )
    result = {
        "balance": (
        	current_app.managers.mcent_profile.get_balance(
        		session_id
        	)
        )
    }
    return jsonify(result)