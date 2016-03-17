"""
    Look at these 2 endpoints.
    Think about how we architected it, and knowing more about
    network latency, ask questions or suggest
    another way to go about it.
"""

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


@rapid_route('/api/v1/login')
@api.route('/login', methods=['POST'])
@api.route('/v1/login', methods=['POST'])
@lock_pcid()
def login():
    ...
    auth_result = current_app.managers.authentication.authenticate(
        pcid,
        session_type,
        password=params.get("password"),
        session_id=session_id,
        user_agent=user_agent,
        phone_number=params.get("phone_number"),
        email_address=params.get("email_address"),
        device_country=device_country,
        device_info=device_info,
        headers=_get_headers(),
        kraken_version=kraken_version,
        traffic_code=traffic_code,
        referrer_code=referrer_code
    )
    session = auth_result.get("session")
    member = auth_result.get("member")

    unflattened_session, unflattened_member = (
        current_app.managers.members.get_session_member(
            session.get('token'), skip_lock=True
        )
    )

    nar_data = current_app.managers.nar_manager.get_nar_payouts(
        unflattened_member
    )
    checkin_data = current_app.managers.checkin_manager.get_checkin_attributes(
        unflattened_member
    )
    review_prompt_data = current_app.managers.review_prompt(
        unflattened_member
    )
    airtime_gifting_data = (
        current_app.managers.airtime_gifting.fetch_airtime_gifting_attributes(
            unflattened_member
        )
    )
    experiment_data = (
        current_app.managers.tracking_manager.fetch_experiment_data(
            unflattened_member
        )
    )
    mcent_profile = (
        current_app.managers.mcent_profile.get_mcent_profile(
            unflattened_member, flatten=True
        )
    )

    response_result = {
        "session": session,
        "member": member,
        "mcent_profile": mcent_profile,
        "experiment_data": experiment_data,
        "airtime_gifting": airtime_gifting_data,
        "review_prompt_data": review_prompt_data,
        "checkin_data": checkin_data,
        "new_activation_data": new_activation_data,
    }

    return jsonify(response_result)

@rapid_route('/api/v1/get_activity_feed')
@api.route('/get_activity_feed', methods=['POST'])
@api.route('/v1/get_activity_feed', methods=['POST'])
@lock_pcid()
def get_activity_feed():
    session, member = current_app.managers.session.get_session_member(session_id)

    activity_feed_data = current_app.managers.activity_feed.get_activity_feed(
        member
    )
    nar_data = current_app.managers.nar_manager.get_nar_payouts(
        unflattened_member
    )
    checkin_data = current_app.managers.checkin_manager.get_checkin_attributes(
        unflattened_member
    )
    review_prompt_data = current_app.managers.review_prompt(
        unflattened_member
    )
    airtime_gifting_data = (
        current_app.managers.airtime_gifting.fetch_airtime_gifting_attributes(
            unflattened_member
        )
    )
    experiment_data = (
        current_app.managers.tracking_manager.fetch_experiment_data(
            unflattened_member
        )
    )
    mcent_profile = (
        current_app.managers.mcent_profile.get_mcent_profile(
            unflattened_member, flatten=True
        )
    )

    response_result = {
        "member": member,
        "activity_feed": activity_feed,
        "mcent_profile": mcent_profile,
        "experiment_data": experiment_data,
        "airtime_gifting": airtime_gifting_data,
        "review_prompt_data": review_prompt_data,
        "checkin_data": checkin_data,
        "new_activation_data": new_activation_data,
    }



