# 🚀 START HERE - Chat Configuration Guide

Welcome! Your retail store app is ready to use chat with custom OpenAI-compatible APIs.

## ⚡ 5-Minute Quick Start

```bash
# Step 1: Go to UI directory
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui

# Step 2: Create configuration file
cp .env.example .env

# Step 3: Edit .env with your API details
# Replace:
#   - https://api.ai.kodekloud.com (if different)
#   - sk-YOUR-API-KEY-HERE (with your actual key)
nano .env

# Step 4: Verify configuration
./check-config.sh

# Step 5: Start the app
source .env
./mvnw spring-boot:run

# Step 6: Open browser
# Visit http://localhost:8080
```

That's it! Chat should be available in the UI.

---

## 📚 Full Documentation (Pick Your Path)

### I want to understand everything first
1. Read: **CHAT_SETUP_INDEX.md** (navigation guide)
2. Read: **JAVA_IMPLEMENTATION.md** (how the code works)
3. Read: **CONFIG_SUMMARY.md** (reference)
4. Then follow 5-minute quick start above

### I want just the facts and minimal time
1. Copy: `.env.example` → `.env`
2. Edit: Your API key and URL
3. Run: `./check-config.sh` to verify
4. Run: Application
5. Test: Chat in browser

### I'm having problems
1. Run: `./check-config.sh` (diagnoses issues)
2. Read: **TESTING_CHAT.md** (troubleshooting)
3. Check: Application logs
4. Read: **CONFIG_SUMMARY.md** (common problems)

---

## 📖 Documentation Files (All in This Directory)

| File | Purpose | Read Time |
|------|---------|-----------|
| **START_HERE.md** | This file - where to start | 2 min |
| **QUICK_START.md** | Get running immediately | 2 min |
| **SETUP_CUSTOM_API.md** | All configuration options | 10 min |
| **TESTING_CHAT.md** | Verify it works | 15 min |
| **JAVA_IMPLEMENTATION.md** | Code explanation | 20 min |
| **CONFIG_SUMMARY.md** | Reference & troubleshooting | 10 min |
| **CHAT_SETUP_INDEX.md** | Complete navigation guide | 5 min |
| **SETUP_COMPLETE.md** | Detailed summary | 10 min |
| **README.md** | General app information | 5 min |

---

## 🔧 Configuration Quick Reference

### What You Need

```bash
# These 5 are REQUIRED
RETAIL_UI_CHAT_ENABLED=true
RETAIL_UI_CHAT_PROVIDER=openai
RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY-HERE

# These are OPTIONAL (defaults shown)
RETAIL_UI_CHAT_TEMPERATURE=0.6
RETAIL_UI_CHAT_MAX_TOKENS=300
```

### Where to Put It

**Option 1: In .env file (Recommended)**
```bash
cp .env.example .env
# Edit with your values
source .env
./mvnw spring-boot:run
```

**Option 2: Export as environment variables**
```bash
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
# ... set all variables
./mvnw spring-boot:run
```

**Option 3: Pass as command line arguments**
```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="\
  --retail.ui.chat.enabled=true \
  --retail.ui.chat.provider=openai \
  ..."
```

---

## ✅ Verification Checklist

### Before Starting
- [ ] API key obtained from your provider
- [ ] Base URL known (e.g., https://api.ai.kodekloud.com)
- [ ] Model name available at endpoint

### Before Running App
- [ ] .env file created with your values
- [ ] `./check-config.sh` passes all checks

### After Starting App
- [ ] No errors in startup logs
- [ ] Logs show "Creating OpenAI chat client with baseUrl: ..."
- [ ] UI loads at http://localhost:8080
- [ ] Chat widget visible
- [ ] Test message sends and receives response

---

## 🧪 Testing Your Setup

### Quick Test (1 minute)
```bash
# 1. Run verification tool
./check-config.sh

# 2. Check if it passes
# If all green ✓ - you're good to go
# If any red ✗ - fix the issues and try again
```

### Full Test (5 minutes)
See **TESTING_CHAT.md** for detailed testing procedures including:
- API connectivity test
- Browser testing
- Different scenarios
- Performance monitoring

---

## 🐛 Troubleshooting

### Chat widget not visible in UI
**Cause**: Chat not enabled or configuration not loaded  
**Fix**: Check `RETAIL_UI_CHAT_ENABLED=true` and restart app

### "Cannot connect to API" error
**Cause**: Wrong base URL or network issue  
**Fix**: Verify URL is correct, test with: `./check-config.sh`

### "Unauthorized" or 401 error
**Cause**: Invalid API key  
**Fix**: Check your API key is correct and not expired

### "Model not found" error
**Cause**: Model doesn't exist at your endpoint  
**Fix**: Verify model name with your API provider

**Still stuck?** Run: `./check-config.sh` - it usually diagnoses the problem!

---

## 🎯 What to Read When

```
You are new to this project
├─ Read: QUICK_START.md
└─ Read: SETUP_CUSTOM_API.md

You have questions about configuration
├─ Read: CONFIG_SUMMARY.md
├─ Run: ./check-config.sh
└─ Read: SETUP_CUSTOM_API.md

You want to understand the code
├─ Read: JAVA_IMPLEMENTATION.md
└─ Review: src/main/java/com/amazon/sample/ui/config/chat/

You need to troubleshoot problems
├─ Run: ./check-config.sh (first!)
├─ Read: TESTING_CHAT.md
└─ Check: Application logs

You want all the details
├─ Read: CHAT_SETUP_INDEX.md (navigation)
├─ Read: SETUP_COMPLETE.md (summary)
├─ Read: CONFIG_SUMMARY.md (reference)
└─ Read: JAVA_IMPLEMENTATION.md (code)
```

---

## 🛠️ Tools Available

### check-config.sh
Diagnostic tool that checks your configuration:
```bash
./check-config.sh
```

**Verifies**:
- ✓ All required variables are set
- ✓ URL format is valid
- ✓ API endpoint is reachable
- ✓ Authentication is working

### .env.example
Template configuration file:
```bash
cp .env.example .env
```

Edit with your values and you're ready to go.

---

## 🚀 Three Ways to Run

### Method 1: Using .env (Recommended)
```bash
source .env
./mvnw spring-boot:run
```

### Method 2: Inline Variables
```bash
RETAIL_UI_CHAT_ENABLED=true \
RETAIL_UI_CHAT_PROVIDER=openai \
./mvnw spring-boot:run
```

### Method 3: Properties File
Create `src/main/resources/application-local.yml`:
```yaml
retail:
  ui:
    chat:
      enabled: true
      provider: openai
      openai:
        base-url: https://api.ai.kodekloud.com
        api-key: sk-...
```

Run with: `./mvnw spring-boot:run --spring.profiles.active=local`

---

## 📊 System Architecture

```
                    ┌─────────────┐
                    │   Browser   │
                    │     UI      │
                    └──────┬──────┘
                           │
                    /chat/submit endpoint
                           │
                    ┌──────▼──────┐
                    │ChatController
                    └──────┬──────┘
                           │
              ChatClient (dependency injection)
                           │
          ┌─────────────────┴──────────────────┐
          │   OpenAiChatModel                  │
          │   - Custom base URL                │
          │   - API key                        │
          │   - Model & parameters             │
          └────────────────┬────────────────────┘
                           │
            HTTP POST to custom API
                           │
        ┌───────────────────▼────────────────────┐
        │ Your Custom API Endpoint               │
        │ https://api.ai.kodekloud.com        │
        │                                        │
        │ ├─ Authorization: Bearer sk-...        │
        │ ├─ model: claude-haiku-4-5-...        │
        │ ├─ messages: [...]                     │
        │ └─ max_tokens: 300                     │
        └────────────────┬─────────────────────┘
                         │
              Response from AI Model
                         │
                  ┌──────▼──────┐
                  │   Stream    │
                  │   Back to   │
                  │   Browser   │
                  └─────────────┘
```

---

## ✨ Key Features

- ✅ Custom OpenAI-compatible API support
- ✅ Works with any model at your endpoint
- ✅ Configurable temperature and max tokens
- ✅ Real-time streaming responses
- ✅ Server-Sent Events support
- ✅ AWS Bedrock support (alternative)
- ✅ Mock provider for testing

---

## 🔐 Security Tips

- ✅ Use environment variables for secrets
- ✅ Add `.env` to `.gitignore`
- ✅ Use HTTPS in production
- ✅ Rotate API keys regularly
- ✅ Monitor API usage

---

## 🎓 Learning Path

### Level 1: Get It Working
1. Run 5-minute quick start above
2. Test in browser

### Level 2: Understand Configuration
1. Read: SETUP_CUSTOM_API.md
2. Review: .env.example
3. Read: CONFIG_SUMMARY.md

### Level 3: Understand the Code
1. Read: JAVA_IMPLEMENTATION.md
2. Review: Configuration classes
3. Review: ChatController.java

### Level 4: Master Troubleshooting
1. Read: TESTING_CHAT.md
2. Learn: ./check-config.sh
3. Read: CONFIG_SUMMARY.md troubleshooting section

---

## ❓ Common Questions

**Q: Do I need to modify Java code?**
A: No! The existing code already supports custom APIs. Just set environment variables.

**Q: What if my API endpoint is different?**
A: Set `RETAIL_UI_CHAT_OPENAI_BASE_URL` to your endpoint. It works with any OpenAI-compatible API.

**Q: Can I use multiple models?**
A: Yes! Change `RETAIL_UI_CHAT_MODEL` and restart. Works with any model at your endpoint.

**Q: Is the chat feature optional?**
A: Yes. Set `RETAIL_UI_CHAT_ENABLED=false` to disable it.

**Q: What if chat stops working?**
A: Run `./check-config.sh` to diagnose, then check application logs.

---

## 📞 Where to Get Help

### For Quick Answers
→ Run `./check-config.sh`

### For Setup Help
→ Read `QUICK_START.md`

### For Detailed Configuration
→ Read `SETUP_CUSTOM_API.md`

### For Testing Help
→ Read `TESTING_CHAT.md`

### For Code Questions
→ Read `JAVA_IMPLEMENTATION.md`

### For Everything
→ Start with `CHAT_SETUP_INDEX.md`

---

## 🎉 Ready to Go!

You now have everything needed to:
- ✅ Configure custom OpenAI-compatible API
- ✅ Verify your configuration
- ✅ Run the application
- ✅ Test the chat feature
- ✅ Troubleshoot problems
- ✅ Understand how it works
- ✅ Modify if needed

**Next Step**: 
1. Copy `.env.example` to `.env`
2. Edit with your API details
3. Run `./check-config.sh`
4. Start the app!

---

**Questions?** Check the relevant documentation or run `./check-config.sh` to diagnose configuration issues.
