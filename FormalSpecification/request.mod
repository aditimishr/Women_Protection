mod EMERGENCY* {
	protecting(STRING)
	protecting(FLOAT)
	[Request]

	-- allrequest - Defines the request list added for Admin to take action
	-- addrequest - It is just like the notification added in the Admin's request list
	-- firstrequesttoact - gives the name of the person who has sent the request first but not yet attended by Admin
	-- recentaddedrequest - gives the name of the person who has recently send notification to Admin 
	
	-- signatures
	op norequest : -> Request
	op allrequest : Request String Float Float String -> Request
	op addrequest : Request String Float Float String -> Request
	op firstrequesttoact : Request -> String
	op recentaddedrequest : Request -> String
	op empty-request : -> ?String
	op empty-requestadd : -> ?Request
	op _and_ : Bool Bool -> Bool
	op found : Request String -> Bool
	op _==_ : Bool Bool -> Bool
	op empty-request-first : -> ?Bool

	-- axioms
	var R : Request
	var R1 : Request
	var N : String  -- name of person who registered the request
	var S : String -- status of request (sent-which is not seen by admin and seen- which is seen by admin)
	var NA : String -- another variable for name
	var SA : String -- another variable for status
	var La : Float -- latitude of place from where the request is registered
	var Lo : Float -- longitude of place from where the request is registered
	var LaA : Float -- another variable for latitude
	var LoA : Float	 -- another variable for longitude
	
	eq addrequest (norequest,NA,LaA,LoA,SA) = allrequest(norequest,NA,LaA,LoA,SA) .
	eq addrequest (allrequest(norequest,N,La,Lo,S),N,La,Lo,S) = empty-requestadd .
	eq addrequest (allrequest(norequest,N,La,Lo,S),NA,LaA,LoA,SA) = allrequest(allrequest(norequest,N,La,Lo,S),NA,LaA,LoA,SA) .
	eq addrequest (allrequest(R,N,La,Lo,S),N,La,Lo,S) = empty-requestadd .
	eq addrequest (allrequest(R,N,La,Lo,S),NA,LaA,LoA,SA) = allrequest(allrequest(R,N,La,Lo,S),NA,LaA,LoA,SA) .
	eq found (norequest,S) = empty-request-first .
	eq found (allrequest(norequest,N,La,Lo,S),S) = true .
	eq found (allrequest(R,N,La,Lo,S),S) = true .
	eq found (allrequest(R,N,La,Lo,SA),S) = found (R,S) .
	eq firstrequesttoact (norequest) = empty-request .
	ceq firstrequesttoact (allrequest(norequest,N,La,Lo,"Seen")) = empty-request if found(R,"Sent") == empty-request-first .
	ceq firstrequesttoact (allrequest(norequest,N,La,Lo,"Sent")) = N if found(R,"Sent") == empty-request-first .
	ceq firstrequesttoact (allrequest(R,N,La,Lo,"Sent")) = N if found(R,"Sent") == empty-request-first .
	ceq firstrequesttoact (allrequest(R,N,La,Lo,"Sent")) = firstrequesttoact(R) if found(R,"Sent") == true .	
	ceq firstrequesttoact (allrequest(R,N,La,Lo,"Seen")) = firstrequesttoact(R) if found(R,"Sent") == empty-request-first .
	ceq firstrequesttoact (allrequest(R,N,La,Lo,"Seen")) = firstrequesttoact(R) if found(R,"Sent") == true .
	eq recentaddedrequest (norequest) = empty-request .
	eq recentaddedrequest (allrequest(norequest,N,La,Lo,S)) = N .
	eq recentaddedrequest (allrequest(R,N,La,Lo,S)) = N .
}
