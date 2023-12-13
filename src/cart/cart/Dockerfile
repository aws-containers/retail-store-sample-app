FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src
COPY ["cart/cart.csproj", "cart/"]
RUN dotnet restore "cart/cart.csproj"
COPY . .
WORKDIR "/src/cart"
RUN dotnet build "cart.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "cart.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "cart.dll"]
