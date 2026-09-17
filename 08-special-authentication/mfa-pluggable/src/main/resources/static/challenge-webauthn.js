"use strict";

(function () {
	const button = document.getElementById("passkey-signin");
	if (!button) {
		return;
	}

	const csrfHeader = button.dataset.csrfHeader;
	const csrfToken = button.dataset.csrfToken;
	const headers = { "Content-Type": "application/json" };
	headers[csrfHeader] = csrfToken;

	function encode(buffer) {
		const base64 = window.btoa(String.fromCharCode(...new Uint8Array(buffer)));
		return base64.replace(/=/g, "").replace(/\+/g, "-").replace(/\//g, "_");
	}

	function decode(base64url) {
		const base64 = base64url.replace(/-/g, "+").replace(/_/g, "/");
		const binStr = window.atob(base64);
		const bin = new Uint8Array(binStr.length);
		for (let i = 0; i < binStr.length; i++) {
			bin[i] = binStr.charCodeAt(i);
		}
		return bin.buffer;
	}

	async function assertion() {
		const optionsResponse = await fetch("/webauthn/authenticate/options", {
			method: "POST",
			headers
		});
		if (!optionsResponse.ok) {
			throw new Error("Could not load passkey options");
		}
		const options = await optionsResponse.json();
		const allowCredentials = !options.allowCredentials ? [] : options.allowCredentials.map((cred) => ({
			...cred,
			id: decode(cred.id)
		}));
		const cred = await navigator.credentials.get({
			publicKey: {
				...options,
				allowCredentials,
				challenge: decode(options.challenge)
			}
		});
		const { response } = cred;
		return {
			id: cred.id,
			rawId: encode(cred.rawId),
			response: {
				authenticatorData: encode(response.authenticatorData),
				clientDataJSON: encode(response.clientDataJSON),
				signature: encode(response.signature),
				userHandle: response.userHandle ? encode(response.userHandle) : undefined
			},
			type: cred.type,
			clientExtensionResults: cred.getClientExtensionResults(),
			authenticatorAttachment: cred.authenticatorAttachment
		};
	}

	function submit(body) {
		const form = document.createElement("form");
		form.method = "POST";
		form.action = "/challenge";
		const fields = {
			method: "webauthn",
			code: JSON.stringify(body),
			_csrf: csrfToken
		};
		Object.entries(fields).forEach(([name, value]) => {
			const input = document.createElement("input");
			input.type = "hidden";
			input.name = name;
			input.value = value;
			form.appendChild(input);
		});
		document.body.appendChild(form);
		form.submit();
	}

	button.addEventListener("click", async () => {
		button.disabled = true;
		try {
			submit(await assertion());
		} catch (err) {
			console.error(err);
			button.disabled = false;
		}
	});
})();
