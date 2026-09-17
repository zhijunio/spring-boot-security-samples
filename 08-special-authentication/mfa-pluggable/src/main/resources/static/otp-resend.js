(function () {
	document.querySelectorAll("[data-otp-resend]").forEach((button) => {
		const label = "Resend";
		let remaining = Number(button.dataset.cooldown || 0);

		function paint() {
			if (remaining <= 0) {
				button.disabled = false;
				button.textContent = label;
				return false;
			}
			button.disabled = true;
			button.textContent = remaining + "s";
			return true;
		}

		if (!paint()) {
			return;
		}
		const timer = setInterval(() => {
			remaining -= 1;
			if (!paint()) {
				clearInterval(timer);
			}
		}, 1000);
	});
})();
