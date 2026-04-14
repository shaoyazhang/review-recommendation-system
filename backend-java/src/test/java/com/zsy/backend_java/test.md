# Backend API Test Notes

## 1. Check whether the service is running

```powershell
curl.exe http://localhost:8081
```

Expected response:

```text
backend is running
```

## 2. Send phone verification code

```powershell
curl.exe -c cookies.txt -X POST "http://localhost:8081/user/code?phone=612345678"
```

Expected response:

```json
{"success":true,"errorMsg":null,"data":null,"total":null}
```

Then check the backend log and copy the 6-digit verification code.

## 3. Login with phone verification code

Replace `123456` with the real code from the backend log.

```powershell
curl.exe -b cookies.txt -X POST "http://localhost:8081/user/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"phone\":\"612345678\",\"code\":\"123456\"}"
```

## 4. Send email verification code

```powershell
curl.exe -c cookies.txt -X POST "http://localhost:8081/user/code/email?email=example@example.com"
```

Expected response:

```json
{"success":true,"errorMsg":null,"data":null,"total":null}
```

Then check the backend log and copy the 6-digit verification code.

## 5. Login with email verification code

Replace `123456` with the real code from the backend log.

```powershell
curl.exe -b cookies.txt -X POST "http://localhost:8081/user/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"example@example.com\",\"code\":\"123456\"}"
```

## 6. Common error tests

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
curl.exe -b cookies.txt -X POST "http://localhost:8081/user/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"example@example.com\",\"code\":\"000000\"}"
```

## 7. Check current logged-in user from session

After a successful login, use the same `cookies.txt` file to check the current user:

```powershell
curl.exe -b cookies.txt "http://localhost:8081/user/me"
```

## 8. Important note

The verification code is stored in `HttpSession`, so:

- use `-c cookies.txt` when sending the code
- use `-b cookies.txt` when logging in
- keep the same `cookies.txt` file for the same test flow
