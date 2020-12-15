$(function(){
	
	var editId = 0;
	var volleyEntryId = 0;
	
	var entryFlags = {};
	var volleyFlags = {};
	var entryThumbs = {};
	var volleyThumbs = {};
	
	$("#btn-new").click(showNewEntryEditor);
	$("#btn-search").click(search);
	$("#btn-save").click(saveEntry);
	$("#btn-cancel").click(hideEditor);
	$("#btn-delete").click(deleteEntry);
	$("main").on("click", ".edit-entry", showEntryEditor);
	$("main").on("click", ".flag-entry", flagEntry);
	$("main").on("click", ".flag-volley", flagVolley);
	$("main").on("click", ".toggle-book", getVollies);
	$("main").on("click", ".entry-appendable .fa-pencil-alt", showFirstVolleyEditor);
	$("main").on("click", ".volley-appendable .fa-pencil-alt", showNewVolleyEditor);
	$("main").on("click", ".edit-volley", showVolleyEditor);
	$("main").on("click", "#btn-save", saveVolley);
	$("main").on("click", "#btn-delete", deleteVolley);
	$("main").on("click", "#btn-cancel", hideEditor);
		
	getUserEntryFlags();
	getUserVolleyFlags();
	getEntries();
	
	function search() {
		console.log("search");
		var query = $("#search").val();
		$("#search").val("");
		
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
			$(".volley").remove();
		} else {
			$icon.removeClass("fa-book");
			$icon.addClass("fa-book-open");
			
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

		var content = $("#popup-editor textarea").val();
		
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
	
	function saveVolley() {
		
		var content = $(".popup-editor textarea").val();
		
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
	
	function deleteVolley() {
		$.ajax({
			url: "/delete-volley",
			method: "get",
			type: "json",
			data: { id: editId },
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
		getEntries();
		hideEditor();
	}	

	function showNewEntryEditor() {
		hideEditor();		
		$("#btn-delete").hide();
		$("#popup-editor").show();
	}
	
	function showEntryEditor() {
		hideEditor();
		var text = $(this).parent().parent().parent().find(".entry-content").text();		
		$("#popup-editor textarea").val(text);
		$("#btn-delete").show();
		$("#popup-editor").show();
		editId = $(this).data("id");
	}
	
	function showFirstVolleyEditor() {
		hideEditor();
		volleyEntryId = $(this).parent().parent().find(".edit-entry").data("id");
		$("#btn-delete").hide();
		var $popup = $("#popup-editor").clone();
		$popup.removeAttr("id");
		$popup.addClass("popup-editor");
		$(this).parent().parent().after($popup);
		$popup.show();
	}
		
	function showNewVolleyEditor() {
		hideEditor();
		volleyEntryId = $(this).parent().parent().parent().find(".edit-entry").data("id");
		$("#btn-delete").hide();
		var $popup = $("#popup-editor").clone();
		$popup.removeAttr("id");
		$popup.addClass("popup-editor");
		$(this).parent().parent().after($popup);
		$popup.show();
	}
	
	function showVolleyEditor() {
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
			$entry.find(".entry-content").append(data[i].content);
			(data[i].volleyCount == 0) ? $entry.find(".fa-book").hide() : $entry.find(".fa-pencil-alt").hide();
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
			var $volley = $volleyTemplate.clone();
			$volley.removeClass("template-display-volley");
			$volley.addClass("volley");
			$volley.find(".volley-user-name").append(data[i].user.name);
			$volley.find(".volley-editable").append("<i class='fas fa-edit edit-volley' title='Edit'></i><i class='far fa-flag flag-volley' title='Flag as inappropriate'></i>");
			if (!data[i].editable) $volley.find(".edit-volley").hide();
			$volley.find(".volley-content").append(data[i].content);
			$volley.find(".edit-volley").data("id", data[i].id);// Wow !!!
			if (volleyFlags.hasOwnProperty(data[i].id)) {
				$volley.find(".flag-volley").removeClass("far");
				$volley.find(".flag-volley").addClass("fas");
			}
			$volley.find(".volley-appendable").append("<i class='fas fa-pencil-alt' title='Append to story'>");
			$volley.find(".volley-date").append(data[i].date.slice(0,10));
			$volley.find(".volley-date").prop("title", data[i].date.slice(11));
			$volleyTemplate.parent().append($volley);
		}
	}	
});