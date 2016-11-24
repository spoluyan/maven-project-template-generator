$('#useDistributionManagement').change(
		function() {
			toggleForm($(this), $('#useDistributionManagementForm'),
					$('#distributionManagement\\.repository'));
		});

$('#useScm').change(
		function() {
			toggleForm($(this), $('#useScmForm'),
					$('#scm\\.type'));
		});

function toggleForm($checkBox, $form, $select) {
	if ($checkBox.is(':checked')) {
		$form.removeClass('hidden');
		$form.addClass('show');
	} else {
		$form.removeClass('show');
		$form.addClass('hidden');
		$select.val('NONE');
	}
}