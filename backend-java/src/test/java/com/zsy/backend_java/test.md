# Backend API Test Notes

## 1. Check whether the service is running

```powershell
curl.exe http://localhost:8081/
```

Expected response:

```text
backend is running
```

## 2. Send phone verification code

```powershell
curl.exe -X POST "http://localhost:8081/user/code?phone=612345678"
```

Expected response:

```json
{"success":true,"errorMsg":null,"data":null,"total":null}
```

Then check the backend log and copy the 6-digit verification code.

## 3. Login with phone verification code

Replace `123456` with the real code from the backend log.

```powershell
curl.exe -X POST "http://localhost:8081/user/login" `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"612345678\",\"code\":\"123456\"}"
```

Expected response:

```json
{"success":true,"errorMsg":null,"data":"your-token","total":null}
```

Copy the token from the `data` field.

## 4. Send email verification code

```powershell
curl.exe -X POST "http://localhost:8081/user/code/email?email=example@example.com"
```

Expected response:

```json
{"success":true,"errorMsg":null,"data":null,"total":null}
```

Then check the backend log and copy the 6-digit verification code.

## 5. Login with email verification code

Replace `123456` with the real code from the backend log.

```powershell
curl.exe -X POST "http://localhost:8081/user/login" `
  -H "Content-Type: application/json" `
  -d "{\"email\":\"example@example.com\",\"code\":\"123456\"}"
```

## 6. Check current logged-in user with token

```powershell
curl.exe "http://localhost:8081/user/me" `
  -H "authorization: your-token"
```

## 7. Refresh token test with `/`

1. Check the current TTL:

```powershell
curl.exe "http://localhost:8081/user/token/ttl" `
  -H "authorization: your-token"
```

2. Wait 10-20 seconds, then call `/` with the same token:

```powershell
curl.exe "http://localhost:8081/" `
  -H "authorization: your-token"
```

Expected response:

```text
backend is running
```

3. Check the TTL again:

```powershell
curl.exe "http://localhost:8081/user/token/ttl" `
  -H "authorization: your-token"
```

If refresh is working, the second TTL should jump back close to `30` minutes.

## 8. Browser test page

Open:

```text
http://localhost:8081/refresh-token-test.html
```

The page lets you:

- send a phone or email verification code
- paste the code from the backend log
- log in and store the token locally
- view the current token TTL
- call `/` with the token to trigger refresh
- compare TTL before and after refresh

## 9. Common error tests

Invalid phone:

```powershell
curl.exe -X POST "http://localhost:8081/user/code?phone=123"
```

Invalid email:

```powershell
curl.exe -X POST "http://localhost:8081/user/code/email?email=abc"
```

Wrong verification code:

```powershell
curl.exe -X POST "http://localhost:8081/user/login" `
  -H "Content-Type: application/json" `
  -d "{\"email\":\"example@example.com\",\"code\":\"000000\"}"
```

## 10. Important note

The verification code and login token are stored in Redis, not in `HttpSession`.

- you do not need `cookies.txt` for this flow
- every protected request must send `authorization: your-token`
- token refresh happens only when Redis still contains `login:token:your-token`
