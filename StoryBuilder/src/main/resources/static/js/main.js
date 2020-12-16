$(function(){
	
	var editId = 0;
	var volleyEntryId = 0;
	var siblingId = 0;
	
	var entryFlags = {};
	var volleyFlags = {};
	//var entryThumbs = {};
	//var volleyThumbs = {};
	
	$("#btn-new").click(showNewEditor);
	$("#btn-search").click(search);
	$("#btn-save").click(saveEntry);
	$("#btn-cancel").click(hideEditor);
	$("#btn-delete").click(deleteEntry);
	$("main").on("click", ".edit-entry", showEntryEditor);
	$("main").on("click", ".flag-entry", flagEntry);
	$("main").on("click", ".flag-volley", flagVolley);
	$("main").on("click", ".toggle-book", getVollies);
	$("main").on("click", ".entry-appendable .fa-pencil-alt", showAppendEntryEditor);
	$("main").on("click", ".volley-appendable .fa-pencil-alt", showAppendVolleyEditor);
	$("main").on("click", ".edit-volley", showVolleyEditor);
	$("main").on("click", ".entry-append", saveVolley);
	$("main").on("click", ".volley-append", volleyAppend);
	$("main").on("click", "#btn-delete", deleteVolley);
	$("main").on("click", "#btn-cancel", hideEditor);
		
	getUserEntryFlags();
	getUserVolleyFlags();
	getEntries();
	
	function search() {		
		var query = $("#search").val().trim();
		$("#search").val("");
		
		if (query == "") {
			reloadEntries();
		} else {
			$.ajax({
				url: "/search-entries",
				method: "get",
				dataType: "json",
				data: {
					query,
					limit: 100,
					offset: 0
				},
				error: ajaxError,
				success: function(data) {
					$(".entry").remove();
					buildEntries(data);
				}
			});			
		}
	}
	
	function getEntries() {
		$.ajax({
			url: "/get-entries",
			method: "get",
			type: "json",
			error: ajaxError,
			success: buildEntries
		});
	}
	
	function getVollies() {		
		var $icon = $(this);
		var entryId = $icon.parent().parent().find(".edit-entry").data("id");
		var $volleyTemplate = $icon.parent().parent().find(".template-display-volley");

		if ($(this).attr("class").includes("open")) {
			$(".toggle-book").removeClass("fa-book-open");
			$(".toggle-book").addClass("fa-book");
			$(".toggle-book").attr("title", "Open story");
			$(".is-entry").remove();
			$(".volley").remove();
		} else {
			$icon.removeClass("fa-book");
			$icon.addClass("fa-book-open");
			$(".toggle-book").attr("title", "Close story");			
			$.ajax({
				url: "/get-vollies",
				method: "get",
				type: "json",
				data: { entryId },
				error: ajaxError,
				success: function(data) {					
					buildVollies(data, $volleyTemplate);
				}
			});
		}		
	}
	
	function saveEntry() {
		console.log("saveEntry");
		var content = $("#popup-editor textarea").val().trim();
		
		if (content != "") {
			$.ajax({
				url: "/save-entry",
				method: "post",
				type: "json",
				data: {
					content,
					id: editId
				},
				error: ajaxError,
				success: reloadEntries
			});			
		}
	}
	
	function saveVolley() {
		console.log("entryAppend");		
		var content = $(".popup-editor textarea").val().trim();
		
		if (content != "") {
			$.ajax({		
				url: "/save-volley",
				method: "post",
				type: "json",
				data: {
					content: content,
					id: editId,
					entryId: volleyEntryId
				},
				error: ajaxError,
				success: reloadEntries
			});			
		}
	}
	
	function volleyAppend() {
		var content = $(".popup-editor textarea").val().trim();
		
		if (content != "") {
			$.ajax({
		
				url: "/volley-append",
				method: "post",
				type: "json",
				data: {
					content: content,
					id: editId,
					entryId: volleyEntryId,
					siblingId: siblingId
				},
				error: ajaxError,
				success: reloadEntries
			});			
		}
	}
	
	function deleteEntry() {
		$.ajax({
			url: "/delete-entry",
			method: "get",
			type: "json",
			data: { id: editId },
			error: ajaxError,
			success: reloadEntries
		});
	}
	
	/*function deleteVolley() {
		$.ajax({
			url: "/delete-volley",
			method: "get",
			type: "json",
			data: { id: editId },
			error: ajaxError,
			success: reloadEntries
		});
	}*/
	
	function deleteVolley() {		
		var entryId = $(this).parent().parent().find(".edit-entry").data("id");		
		
		$.ajax({
			url: "/delete-volley",
			method: "get",
			type: "json",
			data: {
				id: editId,
				entryId: entryId,
				volleyId: volleyEntryId,
				
			},
			error: ajaxError,
			success: reloadEntries
		});
	}
	
	function flagEntry() {
		var flagged = false;
		editId = $(this).parent().find(".edit-entry").data("id");
		
		if (entryFlags.hasOwnProperty(editId)) {
			delete entryFlags[editId];
		} else {
			entryFlags[editId] = 1;
			flagged = true;
		}
		$.ajax({
			url: "/flag-entry",
			method: "post",
			type: "json",
			data: { 
				id: editId,
				flagged: flagged
			},
			error: ajaxError,
			success: function() {
				saveUserEntryFlags();
				reloadEntries();
			}		
		});
	}
	
	function flagVolley() {		
		var flagged = false;
		editId = $(this).parent().find(".edit-volley").data("id");
		
		if (volleyFlags.hasOwnProperty(editId)) {
			delete volleyFlags[editId];
		} else {
			volleyFlags[editId] = 1;
			flagged = true;
		}		
		$.ajax({
			url: "/flag-volley",
			method: "post",
			type: "json",
			data: {
				id: editId,
				flagged: flagged
			},
			error: ajaxError,
			success: function() {
				saveUserVolleyFlags();
				reloadEntries();
			}
		});
	}
	
	function getUserEntryFlags() {
		$.ajax({
			url: "/get-entry-flags",
			method: "get",
			type: "json",
			data: {},
			error: ajaxError,
			success: function(data) {
				entryFlags = JSON.parse(data);
			}
		});
	}
	
	function saveUserEntryFlags() {		
		$.ajax({
			url: "/save-entry-flags",
			method: "post",
			type: "json",
			data: {
				entryFlags: JSON.stringify(entryFlags)
			},
			error: ajaxError,
			success: function () {
				console.log("save:",JSON.stringify(entryFlags));
			}
		});
	}
	
	function getUserVolleyFlags() {		
		$.ajax({
			url: "/get-volley-flags",
			method: "get",
			type: "json",
			data: {},
			error: ajaxError,
			success: function(data) {
				volleyFlags = JSON.parse(data);
			}
		});
	}
	
	function saveUserVolleyFlags() {				
		$.ajax({
			url: "/save-volley-flags",
			method: "post",
			type: "json",
			data: {
				volleyFlags: JSON.stringify(volleyFlags)
			},
			error: ajaxError,
			success: function () {
				console.log("save:",JSON.stringify(volleyFlags));
			}
		});
	}
	
	function ajaxError() {
		alert("AJAX ERROR");
	}
	
	function reloadEntries() {
		$(".entry").remove();
		$(".toggle-book").attr("title", "Open story");
		getEntries();
		hideEditor();
	}	

	function showNewEditor() {
		console.log("showNewEditor");
		hideEditor();		
		$("#btn-delete").hide();
		$("#popup-editor").show();
	}
	
	function showEntryEditor() {
		console.log("showEntryEditor");
		hideEditor();
		var text = $(this).parent().parent().parent().find(".entry-content").text();		
		$("#popup-editor textarea").val(text);
		$("#btn-delete").show();
		$("#popup-editor").show();
		editId = $(this).data("id");
	}
	
	function showAppendEntryEditor() {
		console.log("showAppendEntryEditor");
		hideEditor();
		volleyEntryId = $(this).parent().parent().find(".edit-entry").data("id");		
		$("#btn-save").addClass("entry-append");
		$("#btn-save").removeClass("volley-append");
		$("#btn-delete").hide();
		var $popup = $("#popup-editor").clone();
		$popup.removeAttr("id");
		$popup.addClass("popup-editor");
		$(this).parent().parent().after($popup);
		$popup.show();
	}
		
	function showAppendVolleyEditor() {
		console.log("showAppendVolleyEditor");
		hideEditor();
		volleyEntryId = $(this).parent().parent().parent().find(".edit-entry").data("id");
		siblingId =  $(this).parent().parent().find(".edit-volley").data("id");
		$("#btn-save").addClass("volley-append");
		$("#btn-save").removeClass("entry-append");
		$("#btn-delete").hide();
		var $popup = $("#popup-editor").clone();
		$popup.removeAttr("id");
		$popup.addClass("popup-editor");
		$(this).parent().parent().after($popup);
		$popup.show();
	}
	
	function showVolleyEditor() {
		console.log("showVolleyEditor");
		volleyEntryId = $(this).parent().parent().prev().find(".edit-volley").data("id");
		hideEditor();
		$("#btn-delete").show();
		var $popup = $("#popup-editor").clone();
		$popup.removeAttr("id");
		$popup.addClass("popup-editor");
		$(this).parent().parent().after($popup);
		var text = $(this).parent().parent().find(".volley-content").text();
		$(".popup-editor textarea").val(text);
		$popup.show();
		editId = $(this).data("id");
	}
	
	function hideEditor() {
		editId = 0;
		$("#popup-editor textarea").val("");
		$(".popup-editor").remove();
		$("#popup-editor").hide();
		
	}
	
	function buildEntries(data) {
		console.log("buildEntries:", data);
		for (var i = 0; i < data.length; i++) {
			var $entry = $("#template-display-entry").clone();
			$entry.removeAttr("id");
			$entry.addClass("entry");
			$entry.find(".entry-user-name").append(data[i].user.name);
			if (data[i].volleyCount > 0 || !data[i].editable) $entry.find(".edit-entry").hide();
			$entry.find(".edit-entry").data("id", data[i].id);// Wow !!!
			if (entryFlags.hasOwnProperty(data[i].id)) {
				$entry.find(".flag-entry").removeClass("far");
				$entry.find(".flag-entry").addClass("fas");
			}
			if (data[i].flagCount > 0) $entry.find(".entry-flag-count").append(data[i].flagCount);
			$entry.find(".entry-content").append(data[i].content);
			if (data[i].volleyCount < 1) $entry.find(".fa-book").hide();
			if (data[i].volleyCount > 1) $entry.find(".volley-count").append(data[i].volleyCount);
			$entry.find(".entry-date").append(data[i].date.slice(0,10))
			$entry.find(".entry-date").prop("title", data[i].date.slice(11));
			$("main").append($entry);
		}
	}
	
	function buildVollies(data, $volleyTemplate) {
		console.log("buildVollies:", data);		
		hideEditor();
		$volleyTemplate.parent().find(".volley").remove();
				
		for (var i = 0; i < data.length; i++) {
			
			if (data[i].hidden == false) {
				var $volley = $volleyTemplate.clone();
				$volley.removeClass("template-display-volley");
				//$volley.addClass("volley");
				data[i].isEntry ? $volley.addClass("is-entry") : $volley.addClass("volley");
				$volley.find(".volley-user-name").append(data[i].user.name);
				$volley.find(".volley-editable").append("<i class='fas fa-edit edit-volley' title='Edit'></i><i class='far fa-flag flag-volley' title='Flag as inappropriate'></i><span class='volley-flag-count'></span>");
				if (!data[i].editable || data[i].isEntry) $volley.find(".edit-volley").hide();
				$volley.find(".volley-content").append(data[i].content);
				$volley.find(".edit-volley").data("id", data[i].id);// Wow !!!
				if (volleyFlags.hasOwnProperty(data[i].id)) {
					$volley.find(".flag-volley").removeClass("far");
					$volley.find(".flag-volley").addClass("fas");
				}
				if (data[i].flagCount > 0) $volley.find(".volley-flag-count").append(data[i].flagCount);
				$volley.find(".volley-appendable").append("<i class='fas fa-pencil-alt' title='Append to volley'>");
				$volley.find(".volley-date").append(data[i].date.slice(0,10));
				$volley.find(".volley-date").prop("title", data[i].date.slice(11));
				$volleyTemplate.parent().append($volley);				
			}
		}
	}	
});