FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["DotNetCart/DotNetCart.csproj", "DotNetCart/"]
RUN dotnet restore "DotNetCart/DotNetCart.csproj"
COPY . .
WORKDIR "/src/DotNetCart"
RUN dotnet build "DotNetCart.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "DotNetCart.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "DotNetCart.exe"]
