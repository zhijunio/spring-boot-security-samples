(function () {
	const selected = document.getElementById("mnemonic-selected");
	const bank = document.getElementById("mnemonic-bank");
	const form = document.getElementById("confirm-form");
	const code = document.getElementById("code");
	const confirm = document.getElementById("confirm");
	const progress = document.getElementById("mnemonic-progress");
	const clear = document.getElementById("mnemonic-clear");
	const slots = [...selected.querySelectorAll("[data-slot]")];

	function filled() {
		return slots.filter((slot) => slot.classList.contains("filled"));
	}

	function nextEmpty() {
		return slots.find((slot) => slot.classList.contains("empty"));
	}

	function chipByIndex(index) {
		return bank.querySelector('.mnemonic-chip[data-index="' + index + '"]');
	}

	function setChipUsed(chip, used) {
		chip.disabled = used;
		chip.classList.toggle("used", used);
		chip.setAttribute("aria-pressed", used ? "true" : "false");
	}

	function sync() {
		const words = filled().map((slot) => slot.dataset.word);
		code.value = words.join(" ");
		const complete = words.length === 12;
		confirm.disabled = !complete;
		progress.textContent = words.length + " / 12";
		clear.hidden = words.length === 0;
	}

	function fillSlot(chip) {
		const slot = nextEmpty();
		if (!slot) {
			return;
		}
		slot.classList.remove("empty");
		slot.classList.add("filled");
		slot.dataset.word = chip.dataset.word;
		slot.dataset.chip = chip.dataset.index;
		slot.querySelector(".word").textContent = chip.dataset.word;
		slot.setAttribute("aria-label", "Word " + (Number(slot.dataset.slot) + 1) + ", " + chip.dataset.word
				+ ". Activate to remove.");
		setChipUsed(chip, true);
		sync();
	}

	function clearSlot(slot) {
		if (!slot.classList.contains("filled")) {
			return;
		}
		const chip = chipByIndex(slot.dataset.chip);
		if (chip) {
			setChipUsed(chip, false);
		}
		slot.classList.add("empty");
		slot.classList.remove("filled");
		delete slot.dataset.word;
		delete slot.dataset.chip;
		slot.querySelector(".word").textContent = "";
		slot.setAttribute("aria-label", "Word " + (Number(slot.dataset.slot) + 1) + ", empty");
		sync();
	}

	bank.addEventListener("click", (event) => {
		const chip = event.target.closest(".mnemonic-chip");
		if (!chip || chip.disabled) {
			return;
		}
		fillSlot(chip);
	});

	selected.addEventListener("click", (event) => {
		const slot = event.target.closest("[data-slot]");
		if (slot) {
			clearSlot(slot);
		}
	});

	clear.addEventListener("click", () => {
		filled().slice().reverse().forEach(clearSlot);
	});

	form.addEventListener("submit", (event) => {
		if (confirm.disabled || filled().length !== 12) {
			event.preventDefault();
			return;
		}
		confirm.disabled = true;
		confirm.textContent = "Confirming…";
	});

	sync();
})();
